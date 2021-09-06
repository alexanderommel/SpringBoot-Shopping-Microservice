package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutAttribute;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class CheckoutUpdateChain {

    CheckoutValidationFilter validationFilter;
    CheckoutPersistenceFilter persistenceFilter;


    public CheckoutUpdateChain(){
        validationFilter = new CheckoutValidationFilter(CheckoutValidationType.ATTRIBUTE);
        persistenceFilter = new CheckoutPersistenceFilter(CheckoutPersistenceAction.UPDATE);
    }

    public Checkout doFilter(CheckoutAttribute attribute, HttpSession session){
        validationFilter.setCheckoutAttribute(attribute);
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        if (checkout==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No current Checkout object attached to this session");
        validationFilter.doFilter(checkout,session);
        checkout = persistenceFilter.doFilter(checkout,session);
        session.setAttribute("CHECKOUT",checkout);
        return checkout;
    }
}
