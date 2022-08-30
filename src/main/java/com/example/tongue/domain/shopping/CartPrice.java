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
public class CartPrice implements Serializable {
    private BigDecimal totalPrice;
    private BigDecimal discountedAmount;
    private BigDecimal finalPrice;
    private String currency_code;
}
