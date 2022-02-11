package com.example.tongue.resources.merchant;

import com.example.tongue.core.domain.Position;
import com.example.tongue.core.utilities.RestControllerRoutines;
import com.example.tongue.domain.merchant.Collection;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.integration.orders.OrderServiceBroker;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.integration.shipping.ShippingServiceBroker;
import com.example.tongue.integration.shipping.ShippingSummary;
import com.example.tongue.repositories.merchant.CollectionRepository;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
public class StoreVariantRestController {

    private StoreVariantRepository storeVariantRepository;
    private OrderServiceBroker orderServiceBroker;
    private ShippingServiceBroker shippingServiceBroker;
    private CollectionRepository collectionRepository;
    private DiscountRepository discountRepository;

    public StoreVariantRestController(@Autowired StoreVariantRepository storeVariantRepository,
                                      @Autowired OrderServiceBroker orderServiceBroker,
                                      @Autowired ShippingServiceBroker shippingServiceBroker,
                                      @Autowired CollectionRepository collectionRepository,
                                      @Autowired DiscountRepository discountRepository){

        this.storeVariantRepository= storeVariantRepository;
        this.orderServiceBroker=orderServiceBroker;
        this.shippingServiceBroker=shippingServiceBroker;
        this.collectionRepository=collectionRepository;
        this.discountRepository=discountRepository;
    }


    @GetMapping("/stores")
    public ResponseEntity<Map<String,Object>> getNearestRestaurants(Position position){
        log.info("Retrieving restaurants by condition 'nearest'");
        Map<String, Object> response = new HashMap<>();
        List<StoreVariant> storeVariantList = orderServiceBroker.getNearestRestaurants(position);
        List<ShippingSummary> shippingSummaries = new ArrayList<>();
        if (storeVariantList==null){
            log.info("No restaurants found for position {"+position+"}");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (StoreVariant s:storeVariantList) {
            Position destination = s.getLocation();
            ShippingBrokerResponse brokerResponse =
                    shippingServiceBroker.requestShippingSummary(position,destination);
            if (brokerResponse.getSolved()){
                shippingSummaries.add((ShippingSummary) brokerResponse.getMessage("summary"));
                continue;
            }
            log.info("Shipping Summary query failed for store_variant with id: "+s.getId());
            shippingSummaries.add(null);
        }
        response.put("stores",storeVariantList);
        response.put("shipping_summaries",shippingSummaries);
        log.info("Request status OK");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/stores/{id}/menu")
    public ResponseEntity<Map<String,Object>> getMenu(@PathVariable("id") Long id){
        log.info("Searching menu for store id: "+id);
        Map<String,Object> response = new HashMap<>();
        List<Collection> collections = collectionRepository.findAllByStoreVariantId(id);
        if (collections.isEmpty()){
            log.info("No collections found");
            response.put("error","No collections found for this store");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("response",collections);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stores/{id}/discounts")
    public ResponseEntity<Map<String,Object>> getDiscounts(@PathVariable("id") Long id){
        Map<String,Object> response = new HashMap<>();
        List<Discount> discounts = discountRepository.findAllByStoreVariantId(id);
        if (discounts.isEmpty()){
            log.info("No discounts found");
            response.put("error","No discounts found for this store");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("response",discounts);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
