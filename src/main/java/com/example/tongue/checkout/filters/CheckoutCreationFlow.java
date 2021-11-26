package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.shopping.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.Instant;

@Component
public class CheckoutCreationFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;

    public FlowMessage run(Checkout checkout, HttpSession session){
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        ValidationResponse validationResponse = checkoutValidation.softValidation(checkout);
        if (!validationResponse.isSolved()){
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        checkout = populateDefaultValues(checkout);
        checkoutSession.save(checkout,session);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
        return response;
    }

    private Checkout populateDefaultValues(Checkout checkout){
        checkout.setCreated_at(Instant.now());
        return checkout;
    }

}
