package com.example.tongue.integration.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Payment {
    @Id
    private String paymentId;
    private PaymentStatusCode statusCode;

    public enum PaymentStatusCode{
        P1,P2,P3,P4,P5
    }


}
