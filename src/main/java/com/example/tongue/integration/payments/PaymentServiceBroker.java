package com.example.tongue.integration.payments;

import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.integration.customers.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentServiceBroker {

    public boolean validatePaymentSession(String sessionId){
        log.info("Validating Payment Session");
        return true;
    }

    public Payment createPayment(){
        log.info("Payment Creation Request to Payment Service");
        log.info("Ok");
        return Payment.builder().paymentId("1092").build();
    }

    public boolean updatePaymentAccount(){
        /** Change info such as credit card number, **/
        return true;
    }

    public Payment continueShoppingPayment(Payment payment){
        log.info("Continuing payment flow...");
        log.info("Ok");
        payment.setStatusCode(Payment.PaymentStatusCode.P3);
        return payment;
    }

    public PaymentSession createSession(Customer customer, PaymentInfo paymentInfo){
        log.info("Creating Payment Session");
        PaymentSession session = PaymentSession.builder()
                .username(customer.getUsername())
                .sessionId("2022")
                .hasDebts(false).debts(BigDecimal.valueOf(0.0)).build();
        return session;
    }
}
