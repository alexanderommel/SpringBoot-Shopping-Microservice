package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;

import javax.servlet.http.HttpSession;

public class CheckoutSessionFilter implements CheckoutFilter {

    private void addToSession(Checkout checkout, HttpSession session){
        // Maybe do session validation
        // Set expiration and so on
        session.setAttribute("CHECKOUT",checkout);
    }

    @Override
    public Checkout doFilter(Checkout checkout,HttpSession session) {
        addToSession(checkout,session);
        return checkout;
    }
}
