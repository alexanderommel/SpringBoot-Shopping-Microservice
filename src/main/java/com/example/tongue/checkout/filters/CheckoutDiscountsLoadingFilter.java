package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;

import javax.servlet.http.HttpSession;

public class CheckoutDiscountsLoadingFilter implements CheckoutFilter {
    @Override
    public Checkout doFilter(Checkout checkout, HttpSession session) {
        return checkout;
    }
}
