package com.example.tongue.domain.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo {

    private PaymentMethod paymentMethod;
    private String paymentSession;

    enum PaymentMethod{
        CASH, CREDIT_CARD
    }
}
