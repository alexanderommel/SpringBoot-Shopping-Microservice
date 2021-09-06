package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutPrice;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

public class CheckoutShippingRateFilter implements CheckoutFilter {
    @Override
    public Checkout doFilter(Checkout checkout, HttpSession session) {
        CheckoutPrice checkoutPrice = new CheckoutPrice();
        // Temporal
        BigDecimal shippingRate = BigDecimal.valueOf(2.20);
        checkoutPrice.setShippingTotal(shippingRate);
        return checkout;
    }
}
