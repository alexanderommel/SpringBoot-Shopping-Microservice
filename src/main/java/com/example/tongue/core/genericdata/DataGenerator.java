package com.example.tongue.core.genericdata;

import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.CollectionStatus;
import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class DataGenerator {

    public static List<Collection> generateRandomizedCollections(int size, StoreVariant storeVariant, String prefix){

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

    public static List<Modifier> generateRandomizedModifiers(int size, GroupModifier groupModifier, String prefix){

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

    public static List<GroupModifier> generateGroupModifiers(int mandatorySize, int optionalSize,
                                                             StoreVariant storeVariant, Product product, String prefix){

        log.info("Generating "+mandatorySize
                +" mandatory and "+optionalSize
                +" optional group modifiers for store variant id '"+storeVariant.getId()
                +"' and product id '"+product.getId()
                +"'.");

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

    public static List<Product> generateRandomizedProducts(int size, StoreVariant storeVariant, Collection collection, String prefix){

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
