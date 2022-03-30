package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.ValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Instant;

@Service
@Slf4j
public class CheckoutCreationFlow {
    
    private CheckoutValidation checkoutValidation;
    private CheckoutSession checkoutSession;

    public CheckoutCreationFlow(@Autowired CheckoutValidation checkoutValidation,
                                @Autowired CheckoutSession checkoutSession){
        this.checkoutValidation=checkoutValidation;
        this.checkoutSession=checkoutSession;
    }

    public FlowMessage run(Checkout checkout, HttpSession session){
        log.info("Creating Checkout");
        boolean isNull = checkout == null;
        log.info("Checkout entry is null? -> "+isNull);
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        ValidationResponse validationResponse = checkoutValidation.softValidation(checkout);
        if (!validationResponse.isSolved()){
            log.info("Checkout validation status is -> "+validationResponse.isSolved());
            response.setErrorMessage(validationResponse.getErrorMessage());
            response.setErrorStage("Validation error");
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
