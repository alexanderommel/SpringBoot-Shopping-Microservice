package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.*;
import com.example.tongue.integrations.shipping.ShippingBroker;
import com.example.tongue.integrations.shipping.ShippingBrokerResponse;
import com.example.tongue.integrations.shipping.ShippingServiceBroker;
import com.example.tongue.integrations.shipping.ShippingSummary;
import com.example.tongue.locations.models.Location;
import com.example.tongue.shopping.models.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class CheckoutUpgradeFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;
    @Autowired
    private ShippingBroker shippingBroker;


    public FlowMessage run(CheckoutAttribute checkoutAttribute, HttpSession session){
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        Checkout checkout = checkoutSession.get(session);
        if (checkout==null){
            response.setErrorMessage("You must create a Checkout first");
            return response;
        }
        ValidationResponse validationResponse =checkoutValidation.attributeValidation(checkoutAttribute);
        if (!validationResponse.isSolved()){
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        checkout = addAttributeToCheckout(checkoutAttribute,checkout);

        ShippingBrokerResponse brokerResponse =
                shippingBroker.requestShippingSummary(checkout.getOrigin(), checkout.getDestination());

        if (!brokerResponse.getSolved()){
            response.setErrorMessage(brokerResponse.getErrorMessage());
            return response;
        }
        ShippingSummary summary = (ShippingSummary) brokerResponse.getMessage("summary");
        checkout.getPrice().setShippingTotal(summary.getFee());
        checkout.getPrice().setShippingSubtotal(summary.getFee());
        checkout.setEstimatedDeliveryTime(summary.getDeliveryTime());
        checkout.getCart().updatePrice();
        checkout.updateCheckout();

        checkoutSession.save(checkout,session);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
        return response;
    }

    private Checkout addAttributeToCheckout(CheckoutAttribute attribute,Checkout checkout){
        if (attribute.getName()==CheckoutAttributeName.CART){
            Cart cart = (Cart) attribute.getAttribute();
            checkout.setCart(cart);
        }
        if (attribute.getName()==CheckoutAttributeName.ORIGIN){
            Location origin = (Location) attribute.getAttribute();
            checkout.setOrigin(origin);
        }
        if (attribute.getName()==CheckoutAttributeName.DESTINATION){
            Location destination = (Location) attribute.getAttribute();
            checkout.setDestination(destination);
        }
        return checkout;
    }

}
