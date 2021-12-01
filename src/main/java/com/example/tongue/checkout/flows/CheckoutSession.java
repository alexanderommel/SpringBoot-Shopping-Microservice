package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;

import javax.servlet.http.HttpSession;

public class CheckoutSession {

    public Checkout save(Checkout checkout, HttpSession session){
        session.setAttribute("CHECKOUT",checkout); return checkout;
    }

    public Checkout get(HttpSession session){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        return checkout;
    }

    public void delete(HttpSession session){
        session.removeAttribute("CHECKOUT");
    }

}
