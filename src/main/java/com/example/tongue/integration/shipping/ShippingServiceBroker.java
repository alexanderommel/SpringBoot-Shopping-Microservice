package com.example.tongue.integration.shipping;

import com.example.tongue.domain.checkout.Position;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ShippingServiceBroker{

    //Fields
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String shippingSummaryEndpoint;
    private String sessionValidationEndpoint;

    public ShippingServiceBroker(@Autowired RestTemplate restTemplate,
                                 @Autowired ObjectMapper objectMapper,
                                 @Value("${shipping.service.paths.summary}") String shippingSummaryEndpoint,
                                 @Value("${shipping.service.paths.token.validation}") String sessionValidationEndpoint){
        this.restTemplate=restTemplate;
        this.objectMapper=objectMapper;
        this.shippingSummaryEndpoint=shippingSummaryEndpoint;
        this.sessionValidationEndpoint=sessionValidationEndpoint;
    }

    public ShippingBrokerResponse requestShippingSummary(Position origin, Position destination) {
        log.info("Http Call to Shipping Service with reason (ShippingSummary)");
        ShippingBrokerResponse response = new ShippingBrokerResponse();
        response.setIsSolved(false);
        PositionWrapper wrapper = PositionWrapper.builder()
                .origin(origin)
                .destination(destination)
                .build();
        HttpEntity<PositionWrapper> request = new HttpEntity<>(wrapper);
        try{
            ResponseEntity<ShippingSummary> responseEntity = restTemplate.postForEntity(
                    "http://localhost:8088"+shippingSummaryEndpoint,
                    request,
                    ShippingSummary.class
            );
            response.getMessages().put("summary",responseEntity.getBody());
        }catch (RestClientResponseException e) {
            log.info(e.getMessage());
            response.setErrorMessage(e.getMessage());
            response.setStatusCode(e.getRawStatusCode());
            return response;
        }
        response.setIsSolved(true);
        response.setStatusCode(HttpStatus.OK.value());
        log.info("Http request status 200 OK");
        return response;
    }

    public boolean validateShippingSession(String sessionId) throws RestClientResponseException{
        log.info("Validating Shipping Session");
        log.info("Session id is: "+sessionId);
        ResponseEntity<String> entity;
        Map<String,String> params = new HashMap<>();
        params.put("sessionId",sessionId);
        entity =  restTemplate.getForEntity(
                "http://localhost:8088"+sessionValidationEndpoint+"?sessionId="+sessionId,
                String.class
        );
        if (entity.getStatusCode()!=HttpStatus.OK){
            log.info("Invalid session");
            return false;
        }
        log.info("Valid session");
        return true;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PositionWrapper{
        Position origin;
        Position destination;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShippingServiceResponse<T>{
        T response;
        String error;
    }

}
