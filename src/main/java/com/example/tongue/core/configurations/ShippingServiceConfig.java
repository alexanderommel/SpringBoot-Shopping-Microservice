package com.example.tongue.core.configurations;

import com.example.tongue.integrations.shipping.ShippingBroker;
import com.example.tongue.integrations.shipping.ShippingServiceBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class ShippingServiceConfig {

    @Bean
    public ShippingBroker serviceBroker(@Autowired RestTemplate restTemplate){
        return new ShippingServiceBroker(restTemplate);
    }


}
