package com.example.tongue.checkout.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutPrice {
    
    private BigDecimal cartTotal;
    private BigDecimal cartSubtotal;
    private BigDecimal shippingTotal;
    private BigDecimal shippingSubtotal;
    private BigDecimal checkoutTotal;
    private BigDecimal checkoutSubtotal;
    
}
