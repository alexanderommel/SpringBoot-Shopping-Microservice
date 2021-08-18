package com.example.tongue.sales.models;

import javax.persistence.Embeddable;

@Embeddable
public class LineItemPrice {
    private Double unitPrice;
    private Double totalPrice;
    private Double unitDiscountedAmount=0.0;
    private Double totalDiscountedAmount=0.0;
    private Double finalPrice;
    private String currency_code="USD";

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getUnitDiscountedAmount() {
        return unitDiscountedAmount;
    }

    public void setUnitDiscountedAmount(Double unitDiscountedAmount) {
        this.unitDiscountedAmount = unitDiscountedAmount;
    }

    public Double getTotalDiscountedAmount() {
        return totalDiscountedAmount;
    }

    public void setTotalDiscountedAmount(Double totalDiscountedAmount) {
        this.totalDiscountedAmount = totalDiscountedAmount;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void update(){

    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
