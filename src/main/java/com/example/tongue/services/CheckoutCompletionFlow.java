package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.ValidationResponse;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Instant;

@Component
@Slf4j
public class CheckoutCompletionFlow {

    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;
    @Autowired
    private CheckoutRepository checkoutRepository;

    public FlowMessage run(HttpSession httpSession, Customer customer){
        log.info("Checkout completion on process...");
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
        checkout = persistCheckout(checkout,httpSession,customer);
        checkoutSession.delete(httpSession);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
        log.info("Process finished successfully");
        return response;
    }

    private Checkout persistCheckout(Checkout checkout,HttpSession httpSession,Customer customer){
        log.info("Persisting Checkout on Database...");
        log.info("Customer username is: "+customer.getUsername());
        checkout.setFinishedAt(Instant.now());
        checkout.setCustomer(customer);
        Checkout checkout1 = checkoutRepository.save(checkout);
        checkoutSession.save(checkout,httpSession);
        return checkout1;
    }

}
