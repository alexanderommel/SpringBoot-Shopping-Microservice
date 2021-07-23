package com.example.tongue.sales.checkout;

import com.example.tongue.locations.models.Location;

import javax.servlet.http.HttpSession;
import java.time.Instant;

public class CheckoutFluxDefinition {

    public CheckoutBindingMessage createCheckout(HttpSession session, Checkout checkout){
        CheckoutBindingMessage bindingMessage = new CheckoutBindingMessage();
        Checkout sessionCheckout = new Checkout();
        //Fill checkout with basic values
        Boolean isLocationValid = checkout.getOrigin().validate();
        sessionCheckout.setOrigin(checkout.getOrigin());
        sessionCheckout.setDestination(checkout.getOrigin());
        Boolean storeVariantExists = Boolean.TRUE;
        sessionCheckout.setStoreVariant(checkout.getStoreVariant());
        sessionCheckout.setPrice(new CheckoutPrice());
        sessionCheckout.setCreated_at(Instant.now());
        session.setAttribute("CHECKOUT",checkout);
        bindingMessage.setCheckout(sessionCheckout);
        return bindingMessage;
    }
}
