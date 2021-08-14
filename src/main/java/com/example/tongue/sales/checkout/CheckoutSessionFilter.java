package com.example.tongue.sales.checkout;

import javax.servlet.http.HttpSession;

public class CheckoutSessionFilter implements CheckoutFilter{

    private HttpSession session;

    public CheckoutSessionFilter(HttpSession session){
        this.session = session;
    }

    private void addToSession(Checkout checkout){
        // Maybe do session validation
        // Set expiration and so on
        session.setAttribute("CHECKOUT",checkout);
    }

    @Override
    public Checkout doFilter(Checkout checkout) {
        addToSession(checkout);
        return checkout;
    }
}
