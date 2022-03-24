package com.example.tongue.integration.shipping;

import com.example.tongue.core.domain.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingRequest {

    private Artifact artifact;
    private String shippingFeeToken;
    private Billing billing;
    private Position origin;
    private Position destination;
    private Boolean testing;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Billing{

        BigDecimal total;
        BigDecimal artifact;
        BigDecimal fee;
        BigDecimal debt;
        Boolean hasDebts;
        PaymentMethod paymentMethod;

    }

    public enum PaymentMethod{
        CASH,CREDIT
    }
}
