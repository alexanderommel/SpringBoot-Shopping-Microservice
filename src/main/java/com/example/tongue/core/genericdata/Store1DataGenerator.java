package com.example.tongue.core.genericdata;

import com.example.tongue.core.domain.Position;
import com.example.tongue.merchants.enumerations.CollectionStatus;
import com.example.tongue.merchants.enumerations.GroupModifierType;
import com.example.tongue.merchants.enumerations.ProductStatus;
import com.example.tongue.merchants.models.*;
import com.example.tongue.merchants.repositories.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Store1DataGenerator {
    /***
    THIS CLASS GENERATES FULL DATA FOR JUST ONE STORE
     ***/
    private static Store1DataGenerator instance;
    private Store1DataGenerator(){
    }
    public static Store1DataGenerator getInstance(ProductRepository productRepository,
                                                  StoreRepository storeRepository,
                                                  MerchantRepository merchantRepository,
                                                  StoreVariantRepository storeVariantRepository,
                                                  DiscountRepository discountRepository,
                                                  ProductImageRepository imageRepository,
                                                  GroupModifierRepository groupModifierRepository,
                                                  ModifierRepository modifierRepository,
                                                  CollectionRepository collectionRepository,
                                                  CollectionProductAllocationRepository
                                                  collectionProductAllocationRepository){

        if(instance==null){
            instance = new Store1DataGenerator();


            Merchant merchant = new Merchant();
            merchant.setOwnerName("Alexander");
            merchant.setEmail("alexander.rommel@youknowthat.com");
            merchant.setPhoneNumber("+593199819981");
            merchant = merchantRepository.save(merchant);

            Store store = new Store();
            store.setName("Alexander Monster I.C.");
            store.setOwner("Mr.Chicken");
            store.setDomain("www.mrchickenownit.com");
            store.setContactEmail("mr.fish@gmail.com");
            store.setMerchant(merchant);
            store = storeRepository.save(store);

            Position location =
                    Position.builder().latitude(100F).longitude(100F).address("Ibarra").build();

            StoreVariant storeVariant = new StoreVariant();
            storeVariant.setStore(store);
            storeVariant.setLocation(location);
            storeVariant.setName("Sushi Alexander 6 de Diciembre");
            storeVariant.setAllowCashPayments(true);
            storeVariant.setHasActiveDiscounts(false);
            storeVariant.setRepresentative("Nobody");
            storeVariant.setStoreImageURL("findOneBeforePassingToAndroid");
            storeVariant = storeVariantRepository.save(storeVariant);
            System.out.println("StoreVariant Generic id: "+storeVariant.getId());


            String[] titles = {"Product1","Product2","Product3","Product4","Product5","Product6",
                    "Product7"};

            String[] descriptions = {"Description1","Description2","Description3"
                    ,"Description4","Description5","Description6", "Description7"};

            BigDecimal[] prices = {BigDecimal.valueOf(25.5),BigDecimal.valueOf(5.35),BigDecimal.valueOf(10.50),BigDecimal.valueOf(9.00)
                    ,BigDecimal.valueOf(3.50),BigDecimal.valueOf(12.25),BigDecimal.valueOf(10.0)};

            List<ProductStatus> statusList = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                statusList.add(ProductStatus.ACTIVE);
            }

            //COLLECTIONS
            Collection collection1 = new Collection();
            Collection collection2 = new Collection();
            Collection collection3 = new Collection();
            collection1.setStatus(CollectionStatus.ACTIVE);
            collection1.setStoreVariant(storeVariant);
            collection1.setTitle("Hamburgers");
            collection2.setStatus(CollectionStatus.ACTIVE);
            collection2.setStoreVariant(storeVariant);
            collection3.setStatus(CollectionStatus.ACTIVE);
            collection2.setTitle("Pizzas");
            collection3.setTitle("Sushi");
            collection3.setStoreVariant(storeVariant);
            collection1 = collectionRepository.save(collection1);
            collection2 = collectionRepository.save(collection2);
            collection3 = collectionRepository.save(collection3);
            List<Collection> collectionList = new ArrayList<>();
            collectionList.add(collection1);
            collectionList.add(collection2);
            collectionList.add(collection3);

            for (int j =0; j<3;j++){


                List<Product> productList = ProductsGenerator.createProducts(storeVariant,productRepository,
                        titles,descriptions,prices,statusList,collectionList.get(j),
                        collectionProductAllocationRepository);

                List<GroupModifierType> groupModifierTypes = new ArrayList<>();
                groupModifierTypes.add(GroupModifierType.MANDATORY);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);
                groupModifierTypes.add(GroupModifierType.OPTIONAL);

                for (int i = 0; i < productList.size(); i++) {
                    ModifiersGenerator.generateGroupModifiers(productList.get(i),groupModifierTypes,
                            groupModifierRepository,modifierRepository,i%4+1,storeVariant);
                }


                String[] sources = {"source1.http","source2.http","source3.http","source4.http","source5.http",
                        "source6.http","source7.http"};
                List<ProductImage> productImages = ProductImagesGenerator.createImages(imageRepository,productList,sources);



            }

        }

        return instance;

    }

}
