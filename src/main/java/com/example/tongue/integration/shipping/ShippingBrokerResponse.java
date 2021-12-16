package com.example.tongue.integration.shipping;

import java.util.HashMap;
import java.util.Map;

public class ShippingBrokerResponse {

    private String errorMessage;
    private int statusCode;
    private Boolean isSolved;
    private Map<String,Object>  messages;

    public ShippingBrokerResponse(){
        this.messages = new HashMap<>();
    }

    public void addMessage(String key,Object message){
        messages.put(key,message);
    }

    public Object getMessage(String key){
        return messages.get((String) key);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getSolved() {
        return isSolved;
    }

    public void setSolved(Boolean solved) {
        isSolved = solved;
    }
}
