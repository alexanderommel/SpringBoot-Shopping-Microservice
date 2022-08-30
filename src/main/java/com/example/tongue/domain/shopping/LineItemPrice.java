package com.example.tongue.domain.shopping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineItemPrice implements Serializable {

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal unitDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal totalDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal finalPrice;

}
