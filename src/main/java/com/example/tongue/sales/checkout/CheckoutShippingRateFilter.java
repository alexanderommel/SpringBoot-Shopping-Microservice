package com.example.tongue.sales.checkout;

import java.math.BigDecimal;

public class CheckoutShippingRateFilter implements CheckoutFilter{
    @Override
    public Checkout doFilter(Checkout checkout) {
        CheckoutPrice checkoutPrice = new CheckoutPrice();
        // Temporal
        BigDecimal shippingRate = BigDecimal.valueOf(2.20);
        checkoutPrice.setShippingTotal(shippingRate);
        return checkout;
    }
}
