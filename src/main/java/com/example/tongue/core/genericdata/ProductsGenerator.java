package com.example.tongue.core.genericdata;

import com.example.tongue.domain.merchant.Collection;
import com.example.tongue.domain.merchant.CollectionProductAllocation;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.CollectionProductAllocationRepository;
import com.example.tongue.repositories.merchant.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsGenerator {
    public static ArrayList<Product> createProducts(StoreVariant storeVariant,
                                                    ProductRepository productRepository,
                                                    String[] titles,
                                                    String[] descriptions,
                                                    BigDecimal[] prices,
                                                    List<ProductStatus> statusList,
                                                    Collection collection,
                                                    CollectionProductAllocationRepository repository){


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
            CollectionProductAllocation allocation = new CollectionProductAllocation();
            allocation.setProduct(product);
            allocation.setCollection(collection);
            repository.save(allocation);
            products.add(product);
        }
        return products;
    }

}
