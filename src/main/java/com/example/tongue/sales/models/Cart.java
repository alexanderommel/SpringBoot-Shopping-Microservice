package com.example.tongue.sales.models;

import com.example.tongue.merchants.enumerations.DiscountType;
import com.example.tongue.merchants.enumerations.ValueType;
import com.example.tongue.merchants.models.Discount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {

    private @Id @GeneratedValue Long id;

    @OneToMany
    private List<LineItem> items= new ArrayList<>();

    private String instructions=null;

    @ManyToOne
    private Discount discount=null;

    @Embedded
    private CartPrice price=new CartPrice();

    @JsonIgnore
    public void addItem(LineItem lineItem){
        items.add(lineItem);
    }

    @JsonIgnore
    public void updatePrice(){
        if (discount!=null){
            BigDecimal itemsFinalPriceWithDiscountsIgnored = BigDecimal.ZERO;
            for (LineItem item: items){
                item.ignoreDiscountAndUpdatePrice();
                BigDecimal itemFinalPrice = item.getPrice().getFinalPrice();
                itemsFinalPriceWithDiscountsIgnored =
                        itemsFinalPriceWithDiscountsIgnored.add(itemFinalPrice);
            }
            price.setTotalPrice(itemsFinalPriceWithDiscountsIgnored);
            ValueType discountType = discount.getValueType();
            //fixed_amount,percentage
            if (discountType==ValueType.FIXED_AMOUNT){
                price.setDiscountedAmount(discount.getValue());
            }else {
                BigDecimal percentage = discount.getValue();
                BigDecimal amountDiscounted = percentage.multiply(price.getTotalPrice());
                price.setDiscountedAmount(amountDiscounted);
            }
            price.setFinalPrice(price.getTotalPrice().subtract(price.getDiscountedAmount()));
        }else {
            BigDecimal itemsCumulatedTotalPrice = BigDecimal.ZERO;
            BigDecimal itemsCumulatedDiscountedPrice = BigDecimal.ZERO;
            for (LineItem item:items){
                item.updatePrice();
                BigDecimal itemTotalPrice = item.getPrice().getTotalPrice();
                BigDecimal itemDiscountedPrice = item.getPrice().getTotalDiscountedAmount();
                itemsCumulatedTotalPrice =
                        itemsCumulatedTotalPrice.add(itemTotalPrice);
                itemsCumulatedDiscountedPrice =
                        itemsCumulatedDiscountedPrice.add(itemDiscountedPrice);
            }
            price.setTotalPrice(itemsCumulatedTotalPrice);
            price.setDiscountedAmount(itemsCumulatedDiscountedPrice);
            price.setFinalPrice(price.getTotalPrice().subtract(price.getDiscountedAmount()));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
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

    public CartPrice getPrice() {
        return price;
    }

    public void setPrice(CartPrice price) {
        this.price = price;
    }
}
