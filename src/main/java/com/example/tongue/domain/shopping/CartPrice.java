package com.example.tongue.domain.shopping;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class CartPrice {
    private BigDecimal totalPrice;
    private BigDecimal discountedAmount=BigDecimal.ZERO;
    private BigDecimal finalPrice;
    private String currency_code="USD";

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(BigDecimal discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
}
