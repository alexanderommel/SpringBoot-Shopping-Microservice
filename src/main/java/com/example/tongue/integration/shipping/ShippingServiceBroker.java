package com.example.tongue.integration.shipping;

import com.example.tongue.domain.checkout.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ShippingServiceBroker{

    //Fields
    private RestTemplate restTemplate;

    public ShippingServiceBroker(@Autowired RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    public ShippingBrokerResponse requestShippingSummary(Position origin, Position destination) {
        log.info("Http Call to Shipping Service with reason (ShippingSummary)");
        ShippingBrokerResponse response = new ShippingBrokerResponse();
        response.setIsSolved(false);
        try{
            ShippingSummary summary = restTemplate.getForObject(
                    "CHANGE THIS!",
                    ShippingSummary.class
            );
            response.getMessages().put("summary",summary);
        }catch (RestClientResponseException e){
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(e.getRawStatusCode());
        }
        response.setIsSolved(true);
        response.setStatusCode(HttpStatus.OK.value());
        log.info("Shipping Service has responded with Shipping Summary!");
        return response;
    }

    public boolean validatePaymentSession(String sessionId){
        log.info("Validating Shipping Session");
        return true;
    }

}
