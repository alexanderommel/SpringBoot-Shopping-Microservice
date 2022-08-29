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

        String images[] = {
                "data:image/jpeg;base64,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",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT5dG5Ook_gpLNB8FtC1Y8qM2KJy9wFl67CAg&usqp=CAU",
                "data:image/jpeg;base64,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",
                "data:image/jpeg;base64,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"
        };

        String descriptions[] = {"A very very very very awesome product, which should be on your hands now!",
                "Whatever","Enjoy the new product, developed by a big team of experienced chefs around the world",
                "It contains 3 slices of cheese and some cool sauces","Special food of the chef","",""};

        ProductStatus status[] = {ProductStatus.ACTIVE,ProductStatus.ACTIVE,ProductStatus.ACTIVE,
                ProductStatus.ACTIVE,ProductStatus.ACTIVE,ProductStatus.ARCHIVED,ProductStatus.DRAFT};

        Random random = new Random(descriptions.length);

        for (int i=0;i<size;i++){

            int rand1 = (int) (Math.random() * (double) descriptions.length);

            int rand2 = (int) (Math.random() * (double) status.length);

            int rand3 = (int) (Math.random() * (double) images.length);

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
                    .imageUrl(images[rand3])
                    .build();

            products.add(p);
        }

        return products;
    }

}
