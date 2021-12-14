package com.example.tongue.shopping.models;

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
public class LineItemPrice {

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal unitDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal totalDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal finalPrice;

}
