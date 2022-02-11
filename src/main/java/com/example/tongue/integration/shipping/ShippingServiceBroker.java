package com.example.tongue.integration.shipping;

import com.example.tongue.core.domain.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ShippingServiceBroker implements ShippingBroker {

    //Fields
    private RestTemplate restTemplate;
    public ShippingServiceBroker(@Autowired RestTemplate restTemplate){
        this.restTemplate=restTemplate;
    }

    @Override
    public ShippingBrokerResponse requestShippingSummary(Position origin, Position destination) {
        log.info("Http Call to Shipping Service with reason (ShippingSummary)");
        ShippingBrokerResponse response = new ShippingBrokerResponse();
        response.setSolved(false);
        try{
            ShippingSummary summary = restTemplate.getForObject(
                    ShippingServiceInformer.shippingSummaryUrl,
                    ShippingSummary.class
            );
            response.addMessage("summary",summary);
        }catch (RestClientResponseException e){
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(e.getRawStatusCode());
        }
        response.setSolved(true);
        response.setStatusCode(HttpStatus.OK.value());
        log.info("Shipping Service has responded with Shipping Summary!");
        return response;
    }

}
