package com.example.tongue.sales.checkout;

public class CheckoutAttribute {

    private Object attribute;
    private CheckoutAttributeName name;

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public CheckoutAttributeName getName() {
        return name;
    }

    public void setName(CheckoutAttributeName name) {
        this.name = name;
    }
}
