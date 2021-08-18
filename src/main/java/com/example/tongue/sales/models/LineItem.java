package com.example.tongue.sales.models;

import com.example.tongue.merchants.models.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LineItem {
    private @Id @GeneratedValue Long id;

    @ManyToOne
    @NotNull
    private Product product;

    @NotNull
    private int quantity=1;

    @Embedded
    private LineItemPrice price=new LineItemPrice();

    private String instructions;

    @ManyToOne
    private Discount discount;

    @ManyToMany
    private List<Modifier> modifiers=new ArrayList<>();

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LineItemPrice getPrice() {
        return price;
    }

    public void setPrice(LineItemPrice price) {
        this.price = price;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @JsonIgnore
    public void ignoreDiscountAndUpdatePrice(){
        Double unitPrice = product.getPrice();
        Double totalPrice = unitPrice * quantity;
        price.setUnitPrice(unitPrice);
        price.setTotalPrice(totalPrice);
        price.setFinalPrice(totalPrice);
    }

    @JsonIgnore
    public void updatePrice(){ // Discounts not ignored
        if (discount==null){
            ignoreDiscountAndUpdatePrice();
            return;
        }
        String type = discount.getValueType();
        DiscountValueType discountValueType;
        if (type.equalsIgnoreCase("fixed_amount")==true)
            discountValueType = DiscountValueType.fixed_amount;
        else
            discountValueType = DiscountValueType.percentage;
        Double totalPrice = quantity* product.getPrice();
        price.setUnitPrice(product.getPrice());
        price.setTotalPrice(totalPrice);
        Double discountedUnitPrice;
        if (discountValueType==DiscountValueType.fixed_amount){
            discountedUnitPrice = discount.getValue();
            price.setUnitDiscountedAmount(discountedUnitPrice);
            price.setTotalDiscountedAmount(quantity*discountedUnitPrice);
        }else {
            discountedUnitPrice = discount.getValue()* product.getPrice();
            discountedUnitPrice = discountedUnitPrice/100.0;
            price.setUnitDiscountedAmount(discountedUnitPrice);
            price.setTotalDiscountedAmount(quantity*discountedUnitPrice);
        }
        price.setFinalPrice(price.getTotalPrice()-price.getTotalDiscountedAmount());
    }

}
