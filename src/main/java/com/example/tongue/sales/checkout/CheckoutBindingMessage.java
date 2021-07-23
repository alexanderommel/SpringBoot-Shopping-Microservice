package com.example.tongue.sales.checkout;

public class CheckoutBindingMessage {

    /**
     * Singleton
     */

    private String message;

    private Checkout checkout;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    public Boolean hasErrors(){
        return false;
    }
}
