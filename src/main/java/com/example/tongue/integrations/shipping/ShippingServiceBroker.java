package com.example.tongue.integrations.shipping;

import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.models.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class ShippingServiceBroker implements ShippingBroker{

    //Fields
    @Autowired
    private RestTemplate restTemplate;
    private ShippingServiceError error;


    @Override
    public ShippingSummary getDeliverySummary(Location origin, Location destination) {
        try{
            ShippingSummary summary = restTemplate.getForObject(
                    ShippingServiceInformer.shippingSummaryUrl,
                    ShippingSummary.class
            );
            return summary;
        }catch (RestClientResponseException e){
            ShippingServiceError serviceError =  new ShippingServiceError();
            serviceError.setCode(e.getRawStatusCode());
            error = serviceError;
        }
        return null;
    }

    @Override
    public ShippingServiceError getErrors() {
        return this.error;
    }
}
