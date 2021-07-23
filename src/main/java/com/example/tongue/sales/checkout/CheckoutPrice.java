package com.example.tongue.sales.checkout;

import javax.persistence.Embeddable;

@Embeddable
public class CheckoutPrice {
    private Double cartTotal;
    private Double cartSubtotal;
    private Double shippingTotal;
    private Double shippingSubtotal;
    private Double checkoutTotal;
    private Double checkoutSubtotal;
}
