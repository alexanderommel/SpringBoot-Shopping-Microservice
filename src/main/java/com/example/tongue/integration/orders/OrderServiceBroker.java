package com.example.tongue.integration.orders;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderServiceBroker {

    private StoreVariantRepository storeVariantRepository;

    public OrderServiceBroker(@Autowired StoreVariantRepository storeVariantRepository){
        this.storeVariantRepository=storeVariantRepository;
    }

    public List<StoreVariant> getNearestRestaurants(Position position){
        log.info("Searching restaurants near to {"+position+"}");
        // Http Call to Order Service to get active restaurants
        return  storeVariantRepository.findAll();
    }
}
