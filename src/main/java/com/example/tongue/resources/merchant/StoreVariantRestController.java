package com.example.tongue.resources.merchant;

import com.example.tongue.core.utilities.RestControllerRoutines;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StoreVariantRestController {

    private StoreVariantRepository storeVariantRepository;
    public StoreVariantRestController(@Autowired StoreVariantRepository storeVariantRepository){
        this.storeVariantRepository= storeVariantRepository;
    }

    @GetMapping(value = "/storevariants",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "50") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<StoreVariant> storeVariantPage = storeVariantRepository.findAll(pageable);
        return RestControllerRoutines.getResponseEntityByPageable(
                storeVariantPage,
                "store_variants");
    }

    @GetMapping(value = "/storevariants", params = {"food_type","page","size"})
    public ResponseEntity<Map<String,Object>> allByFilter(
            @RequestParam StoreVariantType food_type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size){

        Pageable pageable = PageRequest.of(page,size);
        Page<StoreVariant> storeVariantPage =
                storeVariantRepository.findAllByStoreFoodType(food_type,pageable);
        return RestControllerRoutines.getResponseEntityByPageable(
                storeVariantPage,
                "store_variants"
        );

    }


}
