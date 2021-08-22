package com.example.tongue.sales.models;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class LineItemPrice {
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal unitDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal totalDiscountedAmount=BigDecimal.ZERO;
    private BigDecimal finalPrice;
    private String currency_code="USD";

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitDiscountedAmount() {
        return unitDiscountedAmount;
    }

    public void setUnitDiscountedAmount(BigDecimal unitDiscountedAmount) {
        this.unitDiscountedAmount = unitDiscountedAmount;
    }

    public BigDecimal getTotalDiscountedAmount() {
        return totalDiscountedAmount;
    }

    public void setTotalDiscountedAmount(BigDecimal totalDiscountedAmount) {
        this.totalDiscountedAmount = totalDiscountedAmount;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void update(){

    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
}
