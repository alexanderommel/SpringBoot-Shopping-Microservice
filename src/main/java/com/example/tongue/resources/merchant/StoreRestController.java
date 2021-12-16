package com.example.tongue.resources.merchant;

import com.example.tongue.repositories.merchant.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoreRestController {
    StoreRepository storeRepository;
    public StoreRestController(@Autowired StoreRepository storeRepository){
        this.storeRepository = storeRepository;
    }
}
