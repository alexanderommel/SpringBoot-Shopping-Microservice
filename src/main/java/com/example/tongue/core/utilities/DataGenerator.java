package com.example.tongue.core.utilities;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.checkout.ShippingInfo;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.CollectionStatus;
import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.repositories.merchant.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@Slf4j
public class DataGenerator {

    public StoreVariantRepository storeVariantRepository;
    private ProductRepository productRepository;
    private CollectionRepository collectionRepository;
    private GroupModifierRepository groupModifierRepository;
    private ModifierRepository modifierRepository;

    public DataGenerator(@Autowired StoreVariantRepository storeVariantRepository,
                         @Autowired ProductRepository productRepository,
                         @Autowired CollectionRepository collectionRepository,
                         @Autowired GroupModifierRepository groupModifierRepository,
                         @Autowired ModifierRepository modifierRepository){

        this.storeVariantRepository=storeVariantRepository;
        this.productRepository=productRepository;
        this.collectionRepository=collectionRepository;
        this.groupModifierRepository=groupModifierRepository;
        this.modifierRepository=modifierRepository;
    }

    public Checkout generateCheckout(){
        log.info("Generating Random Checkout");
        log.info("Shipping and Payment Sessions won't be valid sessions when created");
        StoreVariant s = storeVariantRepository.findAll().get(0);
        Collection c = collectionRepository.findAllByStoreVariantId(s.getId()).get(0);
        List<Product> products = productRepository.findAllByCollection_IdAndStatus(c.getId(),ProductStatus.ACTIVE);

        log.info("Filling Shopping Cart");

        List<LineItem> items = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (i==4)
                break;

            LineItem l = LineItem.builder()
                    .product(products.get(i))
                    .quantity((i%2)+1)
                    .instructions((i==2?"Extra ketchup":null))
                    .build();

            List<GroupModifier> groupModifiers = groupModifierRepository.findAllByProduct_Id(products.get(i).getId());

            if ((i%2)==0 && !groupModifiers.isEmpty()){
                List<Modifier> modifiers = modifierRepository.findAllByGroupModifier_Id(groupModifiers.get(0).getId());
                l.setModifiers(modifiers);
            }

            items.add(l);
        }

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .items(items)
                .instructions("Take care")
                .build();

        log.info("Filling Shipping Info");
        ShippingInfo shippingInfo = ShippingInfo.builder()
                .customerPosition(Position.builder()
                        .address("Quito")
                        .owner("Alexander")
                        .longitude(1.0000f)
                        .latitude(2.0000f)
                        .build())
                .storePosition(Position.builder()
                        .address("Quito")
                        .owner("Not necessary")
                        .longitude(1.0000f)
                        .latitude(-2.0000f)
                        .build())
                .fee(BigDecimal.ONE)
                .shippingSession("RandomShippingSessionNumber")
                .build();

        log.info("Filling Payment Info");
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .paymentMethod(PaymentInfo.PaymentMethod.CASH)
                .paymentSession("RandomPaymentSessionNumber")
                .build();

        Checkout checkout = Checkout.builder()
                .storeVariant(s)
                .shoppingCart(shoppingCart)
                .shippingInfo(shippingInfo)
                .paymentInfo(paymentInfo)
                .build();

        log.info("Checkout created!");
        return checkout;
    }

    public List<Collection> generateRandomizedCollections(int size, StoreVariant storeVariant, String prefix){

        log.info("Generating "+size
                +" collections for store variant id '"+storeVariant.getId()
                +"'.");

        List<Collection> collectionList = new ArrayList<>();

        for (int i=0;i<size;i++){
            Collection collection = Collection.builder()
                    .storeVariant(storeVariant)
                    .status(CollectionStatus.ACTIVE)
                    .imageUrl("<no-image>")
                    .title(prefix + String.valueOf(i))
                    .build();
            collectionList.add(collection);
        }
        return collectionList;
    }

    public List<Modifier> generateRandomizedModifiers(int size, GroupModifier groupModifier, String prefix){

        //log.info("Generating "+size
        //        +" modifiers for group modifier with id '"+groupModifier.getId()+"'.");

        double prices[] = {0.25,0.50,0.75,1.00,1.00,1.00,1.25,1.25,1.25,1.25,1.50,2.00,3.00,5.00};

        List<Modifier> modifiers = new ArrayList<>();
        for (int i=0;i<size;i++) {

            int rand1 = (int) (Math.random() * (double) prices.length);

            Modifier modifier = Modifier.builder()
                    .name(prefix+String.valueOf(i))
                    .price(BigDecimal.valueOf(prices[rand1]))
                    .groupModifier(groupModifier)
                    .build();

            modifiers.add(modifier);
        }
        return modifiers;
    }

    public List<GroupModifier> generateGroupModifiers(int mandatorySize, int optionalSize,
                                                             StoreVariant storeVariant, Product product, String prefix){

        /*
        log.info("Generating "+mandatorySize
                +" mandatory and "+optionalSize
                +" optional group modifiers for store variant id '"+storeVariant.getId()
                +"' and product id '"+product.getId()
                +"'.");

         */

        List<GroupModifier> groupModifiers = new ArrayList<>();

        for (int i=0;i<mandatorySize;i++){
            GroupModifier groupModifier = GroupModifier.builder()
                    .type(GroupModifierType.MANDATORY)
                    .context(prefix+String.valueOf(i))
                    .product(product)
                    .storeVariant(storeVariant)
                    .maximumActiveModifiers(((i +1)/2)+1)
                    .minimumActiveModifiers(1)
                    .build();
            groupModifiers.add(groupModifier);
        }
        for (int i=0;i<optionalSize;i++){
            GroupModifier groupModifier = GroupModifier.builder()
                    .type(GroupModifierType.OPTIONAL)
                    .context(prefix+String.valueOf(i))
                    .product(product)
                    .storeVariant(storeVariant)
                    .maximumActiveModifiers(i+1)
                    .minimumActiveModifiers(0)
                    .build();
            groupModifiers.add(groupModifier);
        }
        return groupModifiers;
    }

    public List<Product> generateRandomizedProducts(int size, StoreVariant storeVariant, Collection collection, String prefix){

        log.info("Generating "+size
                +" products for store variant id '"+storeVariant.getId()
                +"' and collection id '"+collection.getId()
                +"'.");

        List<Product> products = new ArrayList<>();

        String descriptions[] = {"A very very very very awesome product, which should be on your hands now!",
                "Whatever","Enjoy the new product, developed by a big team of experienced chefs around the world",
                "It contains 3 slices of cheese and some cool sauces","Special food of the chef","",""};

        ProductStatus status[] = {ProductStatus.ACTIVE,ProductStatus.ACTIVE,ProductStatus.ACTIVE,
                ProductStatus.ACTIVE,ProductStatus.ACTIVE,ProductStatus.ARCHIVED,ProductStatus.DRAFT};

        Random random = new Random(descriptions.length);

        for (int i=0;i<size;i++){

            int rand1 = (int) (Math.random() * (double) descriptions.length);

            int rand2 = (int) (Math.random() * (double) status.length);

            double price = (int) ((Math.random() * (double) 10)) + 0.5d;

            Product p = Product.builder()
                    .title(prefix+String.valueOf(i))
                    .description(descriptions[rand1])
                    .status(status[rand2])
                    .price(BigDecimal.valueOf(price))
                    .originalPrice(BigDecimal.valueOf(price))
                    .currency_code("USD")
                    .storeVariant(storeVariant)
                    .collection(collection)
                    .build();

            products.add(p);
        }

        return products;
    }

}
