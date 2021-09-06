package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRequestChain {

    private List<CheckoutFilter> filters;

    public CheckoutRequestChain(){
        this.filters = new ArrayList<>();
        CheckoutValidationFilter validationFilter = new CheckoutValidationFilter(CheckoutValidationType.SIMPLE);
        CheckoutSessionFilter sessionFilter = new CheckoutSessionFilter();
        filters.add(validationFilter);
        filters.add(sessionFilter);
    }


    public Checkout doFilter(Checkout checkout, HttpSession httpSession){
        for (CheckoutFilter filter:filters) {
            checkout = filter.doFilter(checkout,httpSession);
        }
        return checkout;
    }
}
