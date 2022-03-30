package com.example.tongue.services;

import com.example.tongue.domain.merchant.Product;
import com.example.tongue.repositories.merchant.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class ProductPersistenceService {

    private ProductRepository productRepository;

    public ProductPersistenceService(@Autowired ProductRepository productRepository){
        this.productRepository=productRepository;
    }

    /** To create a new Product, it must not have an id **/
    public Product create(Product product) throws Exception {
        log.info("Creating Product");
        if (product.getId()==null){
            Instant instant = Instant.now();
            String adjustment = "{price:"+product.getPrice()+",date:"+instant+"}";
            product.setCreatedAt(instant);
            product.setAdjustments(adjustment);
            product.setOriginalPrice(product.getPrice());
            Product product1 = productRepository.save(product);
            return product1;
        }
        throw new Exception("The product couldn't be persisted");
    }

    /** There's some fields which can't be modified **/
    public Product update(Product product) throws Exception{
        log.info("Updating Product");
        Product p = productRepository.findById(product.getId()).get();
        log.info("Id->"+p.getId());
        product.setOriginalPrice(p.getOriginalPrice());
        product.setAdjustments(p.getAdjustments());
        product.setCurrency_code(p.getCurrency_code());
        product.setStoreVariant(p.getStoreVariant());
        if (!p.getPrice().equals(product.getPrice())){
            log.info("New price detected...updating adjustments"); //{price:25.50,date:25-06-2021}
            Instant instant = Instant.now();
            String adjustment = "{price:"+product.getPrice()+",date:"+instant.toString()+"}";
            String adjustments = product.getAdjustments() + "," + adjustment;
            product.setAdjustments(adjustments);
        }
        Product p1 = productRepository.save(product);
        return p1;
}

}
