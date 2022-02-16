package com.example.tongue.integration.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSession {

    String username;
    String sessionId;
    Boolean hasDebts;
    BigDecimal debts;

}
