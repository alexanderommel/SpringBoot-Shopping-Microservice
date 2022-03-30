package com.example.tongue.integration.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingBrokerResponse {

    private String errorMessage;
    private int statusCode;
    private Boolean isSolved;
    private Map<String,Object>  messages = new HashMap<>();

}
