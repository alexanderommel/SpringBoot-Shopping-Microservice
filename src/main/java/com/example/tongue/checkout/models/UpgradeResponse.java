package com.example.tongue.checkout.models;

import com.example.tongue.shopping.models.Order;

public class UpgradeResponse {

    private String errorMessage;
    private Checkout checkout;
    private Boolean solved;

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

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

}
