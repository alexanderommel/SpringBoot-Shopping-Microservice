package com.example.tongue.checkout.models;

import com.example.tongue.shopping.models.Order;

import java.util.HashMap;
import java.util.Map;

public class FlowMessage {

    private String errorMessage;
    private String errorStage;
    private Boolean solved;
    private Map<String,Object> attributes;

    public FlowMessage(){
        attributes = new HashMap<>();
    }

    public String getErrorStage() {
        return errorStage;
    }

    public void setErrorStage(String errorStage) {
        this.errorStage = errorStage;
    }

    public void setAttribute(Object attribute, String key){
        attributes.put(key,attribute);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public Boolean isSolved(){
        return solved;
    }

    public void setSolved(Boolean solved){
        this.solved = solved;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
