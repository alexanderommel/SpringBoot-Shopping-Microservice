package com.example.tongue.core.configurations;

import com.example.tongue.locations.models.Location;
import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.models.*;
import com.example.tongue.merchants.repositories.*;
import com.example.tongue.sales.checkout.CheckoutRepository;
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
                                   StoreRepository storeRepository,
                                   CheckoutRepository checkoutRepository) {

        ArrayList<Product> products = createProducts();
        ArrayList<ProductImage> images = createImages();
        return args -> {
            //Fill database with one merchant,location,store and storevariant
            Merchant merchant = new Merchant();
            merchant.setOwnerName("Rommel");
            merchant = merchantRepository.save(merchant);

            Location origin = new Location();
            origin = locationRepository.save(origin);

            Store store = new Store();
            store.setMerchant(merchant);
            store.setName("Sushi Alexander");
            store = storeRepository.save(store);

            StoreVariant variant = new StoreVariant();
            variant.setLocation(origin);
            variant.setStore(store);
            variant = variantRepository.save(variant);


            //Fill database with products
            for (int i=0;i<7;i++){
                Product product = products.get(i);
                product.setStoreVariant(variant);
                repository1.save(product);
                log.info("Preloading " + product);
            }


            //Fill database with ProductImages
            List<Product> productsX = repository1.findAll();
            for (int j=0;j<7;j++){
                Product productX = productsX.get(j);
                ProductImage image = images.get(j);
                image.setProduct(productX);
                repository2.save(image);
            }



        };
    }

    private ArrayList<ProductImage> createImages(){
        ArrayList<ProductImage> images = new ArrayList<>();
        String[] srcs = {"source1.http","source2.http","source3.http","source4.http","source5.http",
                "source6.http","source7.http"};
        for (int i=0;i< srcs.length;i++){
            ProductImage image = new ProductImage();
            image.setSource(srcs[i]);
            images.add(image);
        }
        return images;
    }

    private ArrayList<Product> createProducts(){
        String[] titles = {"Product1","Product2","Product3","Product4","Product5","Product6",
                "Product7"};

        String[] descriptions = {"Description1","Description2","Description3"
                ,"Description4","Description5","Description6", "Description7"};

        BigDecimal[] prices = {BigDecimal.valueOf(25.5),BigDecimal.valueOf(5.35),BigDecimal.valueOf(10.50),BigDecimal.valueOf(9.00)
                ,BigDecimal.valueOf(3.50),BigDecimal.valueOf(12.25),BigDecimal.valueOf(10.0)};

        String[] status = {"active","active","active","active","active","active","draft"}; //active|draft|archived

        String[] vendors = {"v1","v1","v3","v1","v1","v1","v1"};

        ArrayList<Product> products = new ArrayList<>();

        for (int i=0;i< titles.length;i++){
            Product product = new Product();
            product.setId(25L);
            product.setTitle(titles[i]);
            product.setDescription(descriptions[i]);
            product.setStatus(status[i]);
            product.setPrice(prices[i]);
            product.setOriginalPrice(prices[i]);
            products.add(product);
        }
        return products;
    }
}
