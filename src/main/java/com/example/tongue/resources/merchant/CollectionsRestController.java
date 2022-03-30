package com.example.tongue.resources.merchant;

import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.repositories.merchant.CollectionRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CollectionsRestController{

    private CollectionRepository collectionRepository;
    private ProductRepository productRepository;

    public CollectionsRestController(@Autowired CollectionRepository collectionRepository,
                                     @Autowired ProductRepository productRepository){

        this.collectionRepository=collectionRepository;
        this.productRepository=productRepository;
    }

    @GetMapping("/collections/{id}/products")
    public ResponseEntity<Map<String,Object>> getProducts(@PathVariable("id") Long id){
        Map<String,Object> response = new HashMap<>();
        List<Product> productList = productRepository.findAllByCollection_IdAndStatus(id, ProductStatus.ACTIVE);
        if (productList.isEmpty()){
            log.info("No products for collection 'id->"+id);
            response.put("error","No products found for this collection");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("response",productList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
