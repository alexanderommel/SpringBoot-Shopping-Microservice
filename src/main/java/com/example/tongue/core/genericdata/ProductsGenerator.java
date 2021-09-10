package com.example.tongue.core.genericdata;

import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.enumerations.ProductStatus;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsGenerator {
    public static ArrayList<Product> createProducts(StoreVariant storeVariant,
                                                    ProductRepository productRepository,
                                                    String[] titles,
                                                    String[] descriptions,
                                                    BigDecimal[] prices,
                                                    List<ProductStatus> statusList){


        ArrayList<Product> products = new ArrayList<>();

        for (int i=0;i< titles.length;i++){
            Product product = new Product();
            product.setTitle(titles[i]);
            product.setDescription(descriptions[i]);
            product.setStatus(statusList.get(i));
            product.setPrice(prices[i]);
            product.setOriginalPrice(prices[i]);
            product.setStoreVariant(storeVariant);
            product = productRepository.save(product);
            products.add(product);
        }
        return products;
    }

}
