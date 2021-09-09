package com.example.tongue.core.genericdata;

import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.repositories.*;

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


        }
        return instance;
    }
}
