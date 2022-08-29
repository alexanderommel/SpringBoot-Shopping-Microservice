package com.example.tongue.resources.merchant;

import com.example.tongue.core.contracts.ApiResponse;
import com.example.tongue.domain.checkout.Position;
import com.example.tongue.core.utilities.RestControllerRoutines;
import com.example.tongue.domain.merchant.Collection;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.TongueStore;
import com.example.tongue.integration.orders.OrderServiceBroker;
import com.example.tongue.integration.shipping.*;
import com.example.tongue.repositories.merchant.CollectionRepository;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
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


    @PostMapping(value = "/stores",consumes = "application/json")
    public ResponseEntity<ApiResponse> getNearestRestaurants(@RequestBody Position position){
        log.info("Retrieving restaurants by condition 'nearest'");
        List<StoreVariant> storeVariantList = orderServiceBroker.getNearestRestaurants(position);
        List<TongueStore> tongueStores = new ArrayList<>();
        if (storeVariantList==null){
            log.info("No restaurants found for position {"+position+"}");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for (StoreVariant s:storeVariantList) {
            ShippingSummary shippingSummary = null;
            Position destination = s.getLocation();
            ShippingBrokerResponse brokerResponse =
                    ShippingBrokerResponse.builder().isSolved(true).build(); // Esta linea no es
                    //shippingServiceBroker.requestShippingSummary(position,destination); // Esta si es de igualar
            if (brokerResponse.getIsSolved()){
                // Esta linea de abajo es solo para pruebas
                shippingSummary = ShippingSummary.builder()
                        .arrivalTime(LocalTime.now().plusMinutes(40))
                        .distance(new Distance(100.0, Metrics.KILOMETERS))
                        .shippingFee(ShippingFee.builder()
                                .fee(BigDecimal.valueOf(3.50))
                                .temporalAccessToken(TemporalAccessToken.builder()
                                        .base64Encoding("Token")
                                        .build())
                                .build())
                        .build();
                // Esta linea de abajo es la correcta
                //shippingSummary = (ShippingSummary) brokerResponse.getMessages().get("summary");
            }

            TongueStore tongueStore = new TongueStore(s,shippingSummary);
            tongueStores.add(tongueStore);
        }
        return ResponseEntity.of(Optional.of(ApiResponse.success(tongueStores)));
    }

    @GetMapping("/stores/{id}/menu")
    public ResponseEntity<ApiResponse> getMenu(@PathVariable("id") Long id){
        log.info("Searching menu for store id: "+id);
        List<Collection> collections = collectionRepository.findAllByStoreVariantId(id);
        if (collections.isEmpty()){
            log.info("No collections found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Request status OK");
        return ResponseEntity.of(Optional.of(ApiResponse.success(collections)));
    }

    @GetMapping("/stores/{id}/discounts")
    public ResponseEntity<Map<String,Object>> getDiscounts(@PathVariable("id") Long id){
        log.info("Searching discounts for store id: "+id);
        Map<String,Object> response = new HashMap<>();
        List<Discount> discounts = discountRepository.findAllByStoreVariantId(id);
        if (discounts.isEmpty()){
            log.info("No discounts found");
            response.put("error","No discounts found for this store");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("response",discounts);
        log.info("Request status OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
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
