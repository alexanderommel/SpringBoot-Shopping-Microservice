package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.ValidationResponse;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.Instant;

@Component
public class CheckoutCompletionFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;
    @Autowired
    private CheckoutRepository checkoutRepository;

    public FlowMessage run(HttpSession httpSession){
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        Checkout checkout = checkoutSession.get(httpSession);
        if (checkout==null){
            response.setErrorMessage("You must create a Checkout first");
            return response;
        }
        ValidationResponse validationResponse =checkoutValidation.hardValidation(checkout);
        if (!validationResponse.isSolved()){
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        checkout = persistCheckout(checkout,httpSession);
        checkoutSession.delete(httpSession);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
        return response;
    }

    private Checkout persistCheckout(Checkout checkout,HttpSession httpSession){
        checkout.setFinishedAt(Instant.now());
        // Add principal to checkout
        Checkout checkout1 = checkoutRepository.save(checkout);
        checkoutSession.save(checkout,httpSession);
        return checkout1;
    }

}
