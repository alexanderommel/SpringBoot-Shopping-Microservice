package com.example.tongue.merchants.webservices;

import com.example.tongue.core.utilities.RestControllerRoutines;
import com.example.tongue.merchants.models.Collection;
import com.example.tongue.merchants.models.CollectionProductAllocation;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.CollectionProductAllocationRepository;
import com.example.tongue.merchants.repositories.CollectionRepository;
import com.example.tongue.merchants.repositories.ProductAllocationOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CollectionsRestController{
    private CollectionRepository collectionRepository;
    private CollectionProductAllocationRepository collectionProductAllocationRepository;
    public CollectionsRestController(@Autowired CollectionRepository collectionRepository,
                                     @Autowired CollectionProductAllocationRepository
                                     collectionProductAllocationRepository){
        this.collectionRepository=collectionRepository;
        this.collectionProductAllocationRepository=collectionProductAllocationRepository;
    }

    @GetMapping(value = "/collections",params = {"store_variant_id"})
    public ResponseEntity<Map<String,Object>> getAllCollections(
            @RequestParam Long store_variant_id
    ){

        Pageable pageable = PageRequest.of(0, 25);
        Page<Collection> collectionPage = collectionRepository.findAllByStoreVariantId(
                store_variant_id,
                pageable);
        return RestControllerRoutines.getResponseEntityByPageable(
                collectionPage,
                "collections");

    }

    @GetMapping(value = "collections/{id}/products")
    public ResponseEntity<Map<String,Object>> getProductsByCollections(@PathVariable Long id){
        //Merchants can put at most 100 products on a single collection
        Pageable pageable = PageRequest.of(0,100);
        Page<CollectionProductAllocation> collectionProductAllocationPage =
                collectionProductAllocationRepository.findAllByCollection_Id(id,pageable);
        return RestControllerRoutines.getResponseEntityByPageable(
                collectionProductAllocationPage,
                "products"
        );
    }
}
