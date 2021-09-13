package com.example.tongue.core.configurations;

import com.example.tongue.core.genericdata.ProductImagesGenerator;
import com.example.tongue.core.genericdata.ProductsGenerator;
import com.example.tongue.core.genericdata.Store1DataGenerator;
import com.example.tongue.locations.models.Location;
import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.models.*;
import com.example.tongue.merchants.repositories.*;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);


    @Bean
    CommandLineRunner initDatabase(ProductRepository repository1,
                                   ProductImageRepository repository2,
                                   LocationRepository locationRepository,
                                   MerchantRepository merchantRepository,
                                   StoreVariantRepository variantRepository,
                                   DiscountRepository discountRepository,
                                   StoreRepository storeRepository,
                                   CheckoutRepository checkoutRepository,
                                   GroupModifierRepository groupModifierRepository,
                                   ModifierRepository modifierRepository) {

        return args -> {
            Store1DataGenerator.getInstance(repository1,
                    storeRepository,
                    merchantRepository,
                    variantRepository,
                    discountRepository,
                    repository2,
                    locationRepository,
                    groupModifierRepository,
                    modifierRepository);

        };
    }

}
