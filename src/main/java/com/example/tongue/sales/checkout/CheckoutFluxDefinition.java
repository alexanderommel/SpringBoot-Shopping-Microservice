package com.example.tongue.sales.checkout;

import com.example.tongue.locations.models.Location;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.List;

public class CheckoutFluxDefinition {

    private List<CheckoutFilter> filters;

    public CheckoutFluxDefinition(List<CheckoutFilter> filters){
        this.filters = filters;
    }

    public Checkout filter(Checkout checkout){
        for (CheckoutFilter filter: filters) {
            checkout = filter.doFilter(checkout);
        }
        return checkout;
    }

}
