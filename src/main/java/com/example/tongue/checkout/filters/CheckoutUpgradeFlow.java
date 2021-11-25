package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.*;
import com.example.tongue.integrations.shipping.ShippingServiceBroker;
import com.example.tongue.integrations.shipping.ShippingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class CheckoutUpgradeFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;


    public UpgradeResponse update(CheckoutAttribute checkoutAttribute, HttpSession session){
        UpgradeResponse response = new UpgradeResponse();
        Checkout checkout = checkoutSession.get(session);
        ValidationResponse validationResponse =checkoutValidation.attributeValidation(checkoutAttribute);
        if (!validationResponse.isSolved()){
            response.setSolved(false);
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        ShippingServiceBroker broker = new ShippingServiceBroker();
        ShippingSummary summary = broker.requestShippingSummary(checkout.getOrigin(), checkout.getDestination());
        checkout.getPrice().setShippingTotal(summary.getFee());
        checkout.getPrice().setShippingSubtotal(summary.getFee());
        checkout.setEstimatedDeliveryTime(summary.getDeliveryTime());
        checkout.getCart().updatePrice();
        checkout.updateCheckout();
        checkoutSession.save(checkout,session);
        response.setCheckout(checkout);
        response.setSolved(true);
        return response;
    }

}
