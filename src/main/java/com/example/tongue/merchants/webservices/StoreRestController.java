package com.example.tongue.merchants.webservices;

import com.example.tongue.merchants.repositories.StoreRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoreRestController {
    StoreRepository storeRepository;
    public StoreRestController(@Autowired StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }
}
