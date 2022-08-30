package com.example.tongue.domain.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo implements Serializable {

    private PaymentMethod paymentMethod;
    private String paymentSession;

    public enum PaymentMethod{
        CASH, CREDIT_CARD
    }
}
