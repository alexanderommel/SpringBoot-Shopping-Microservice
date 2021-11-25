package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSession;

public class CheckoutSession {

    public void save(Checkout checkout, HttpSession session){
        session.setAttribute("CHECKOUT",checkout);
    }

    public Checkout get(HttpSession session){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        return checkout;
    }

    public void delete(HttpSession session){
        session.removeAttribute("CHECKOUT");
    }

}
