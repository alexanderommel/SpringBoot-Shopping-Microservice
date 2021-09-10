package com.example.tongue.core.genericdata;

import com.example.tongue.locations.models.Location;
import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.enumerations.ProductStatus;
import com.example.tongue.merchants.models.*;
import com.example.tongue.merchants.repositories.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Store2DataGenerator {

    /***
     THIS CLASS GENERATES FULL DATA FOR JUST ONE STORE
     ***/
    private static Store2DataGenerator instance;
    private Store2DataGenerator(){
    }
    public static Store2DataGenerator getInstance(ProductRepository productRepository,
                                                  StoreRepository storeRepository,
                                                  MerchantRepository merchantRepository,
                                                  StoreVariantRepository storeVariantRepository,
                                                  DiscountRepository discountRepository,
                                                  ProductImageRepository imageRepository,
                                                  LocationRepository locationRepository){

        if(instance==null){

            instance = new Store2DataGenerator();

            Merchant merchant = new Merchant();
            merchant.setOwnerName("Valeria");
            merchant.setEmail("valeria88@youknowthat.com");
            merchant.setPhoneNumber("+593199819982");
            merchant = merchantRepository.save(merchant);

            Store store = new Store();
            store.setName("Pizzeria Vale");
            store.setOwner("Valeria");
            store.setDomain("www.pizzeriavale.com");
            store.setContactEmail("jessica@gmail.com");
            store.setMerchant(merchant);
            store = storeRepository.save(store);

            Location location = new Location();
            location.setGooglePlaceId("colombia-ecuador");
            location = locationRepository.save(location);

            StoreVariant storeVariant = new StoreVariant();
            storeVariant.setStore(store);
            storeVariant.setLocation(location);
            storeVariant.setName("Pizzeria Vale");
            storeVariant.setAllowCashPayments(true);
            storeVariant.setHasActiveDiscounts(true);
            storeVariant.setRepresentative("Jessica");
            storeVariant.setStoreImageURL("findOneBeforePassingToAndroid");
            storeVariant = storeVariantRepository.save(storeVariant);


            String[] titles = {"Pizza siciliana","Pizza mediterranea","Pizza hawaiana","Pizza 3 sabores","Pizza napolitana",
                    "Pizza cola",
                    "Pizza de la casa"};

            String[] descriptions = {"Description1","Description2","Description3"
                    ,"Description4","Description5","Description6", "Description7"};

            BigDecimal[] prices = {BigDecimal.valueOf(25.5),BigDecimal.valueOf(5.35),BigDecimal.valueOf(10.50),BigDecimal.valueOf(9.00)
                    ,BigDecimal.valueOf(3.50),BigDecimal.valueOf(12.25),BigDecimal.valueOf(10.0)};

            List<ProductStatus> statusList = new ArrayList<>();
            for (int i = 0; i < titles.length; i++) {
                statusList.add(ProductStatus.ACTIVE);
            }

            List<Product> productList = ProductsGenerator.createProducts(storeVariant,productRepository,
                    titles,descriptions,prices,statusList);



            String[] sources = {"source1.http","source2.http","source3.http","source4.http","source5.http",
                    "source6.http","source7.http"};
            List<ProductImage> productImages = ProductImagesGenerator.createImages(imageRepository,productList,sources);


        }
        return instance;
    }
}
