package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class CheckoutSession {

    public Checkout save(Checkout checkout, HttpSession session){
        session.setAttribute("CHECKOUT",checkout); return checkout;
    }

    public Checkout get(HttpSession session){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        return checkout;
    }

    public Checkout delete(HttpSession session){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        session.removeAttribute("CHECKOUT");
        return checkout;
    }

}
