package com.example.tongue.sales.models;

import com.example.tongue.merchants.enumerations.DiscountValueType;
import com.example.tongue.merchants.enumerations.ValueType;
import com.example.tongue.merchants.models.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class LineItem {
    private @Id @GeneratedValue Long id;

    @ManyToOne
    @NotNull
    @JsonIgnoreProperties({"status","tags","inventorId",
            "originalPrice","adjustments","currency_code",
            "price","type","title","handle","description","type"})
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
        BigDecimal modifiersTotal = getModifiersTotal();
        BigDecimal unitPrice = product.getPrice().add(modifiersTotal);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        price.setUnitPrice(unitPrice);
        price.setTotalPrice(totalPrice);
        price.setFinalPrice(totalPrice);
    }


    // Discounts not ignored
    @JsonIgnore
    public void updatePrice(){
        if (discount==null){
            ignoreDiscountAndUpdatePrice();
            return;
        }
        ValueType type = discount.getValueType();
        DiscountValueType discountValueType;
        if (type==ValueType.FIXED_AMOUNT)
            discountValueType = DiscountValueType.fixed_amount;
        else
            discountValueType = DiscountValueType.percentage;
        BigDecimal modifiersTotal = getModifiersTotal();
        BigDecimal unitPrice = product.getPrice().add(modifiersTotal);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        price.setUnitPrice(unitPrice);
        price.setTotalPrice(totalPrice);
        BigDecimal discountedUnitPrice;
        if (discountValueType==DiscountValueType.fixed_amount){
            discountedUnitPrice = discount.getValue();
            price.setUnitDiscountedAmount(discountedUnitPrice);
            price.setTotalDiscountedAmount(discountedUnitPrice.multiply(BigDecimal.valueOf(quantity)));
        }else {
            discountedUnitPrice = discount.getValue().multiply(product.getPrice());
            discountedUnitPrice = discountedUnitPrice.multiply(BigDecimal.valueOf(0.01));
            price.setUnitDiscountedAmount(discountedUnitPrice);
            price.setTotalDiscountedAmount((discountedUnitPrice.multiply(BigDecimal.valueOf(quantity))));
        }
        price.setFinalPrice(price.getTotalPrice().subtract(price.getTotalDiscountedAmount()));
    }

    private BigDecimal getModifiersTotal(){
        BigDecimal total = BigDecimal.valueOf(0.0);
        if (this.modifiers!=null){
            total =modifiers.stream().map(x -> x.getPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);
        }
        return total;
    }

}
