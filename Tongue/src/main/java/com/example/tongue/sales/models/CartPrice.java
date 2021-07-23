package com.example.tongue.sales.models;

import javax.persistence.Embeddable;

@Embeddable
public class CartPrice {
    private Double totalPrice;
    private Double discountedAmount;
    private Double finalPrice;
    private String currency_code="USD";
}
