package com.example.tongue.sales.models;

import com.example.tongue.merchants.models.Discount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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
            Double itemsFinalPriceWithDiscountsIgnored = 0.0;
            for (LineItem item: items){
                item.ignoreDiscountAndUpdatePrice();
                Double itemFinalPrice = item.getPrice().getFinalPrice();
                itemsFinalPriceWithDiscountsIgnored =
                        itemsFinalPriceWithDiscountsIgnored + itemFinalPrice;
            }
            price.setTotalPrice(itemsFinalPriceWithDiscountsIgnored);
            String discountType = discount.getValueType();
            //fixed_amount,percentage
            if (discountType.equalsIgnoreCase("fixed_amount")){
                price.setDiscountedAmount(discount.getValue());
            }else {
                Double percentage = discount.getValue();
                Double amountDiscounted = percentage*price.getTotalPrice();
                price.setDiscountedAmount(amountDiscounted);
            }
            price.setFinalPrice(price.getTotalPrice()-price.getDiscountedAmount());
        }else {
            Double itemsCumulatedTotalPrice = 0.0;
            Double itemsCumulatedDiscountedPrice = 0.0;
            for (LineItem item:items){
                item.updatePrice();
                Double itemTotalPrice = item.getPrice().getTotalPrice();
                Double itemDiscountedPrice = item.getPrice().getTotalDiscountedAmount();
                itemsCumulatedTotalPrice =
                        itemsCumulatedTotalPrice + itemTotalPrice;
                itemsCumulatedDiscountedPrice =
                        itemsCumulatedDiscountedPrice + itemDiscountedPrice;
            }
            price.setTotalPrice(itemsCumulatedTotalPrice);
            price.setDiscountedAmount(itemsCumulatedDiscountedPrice);
            price.setFinalPrice(price.getTotalPrice()-price.getDiscountedAmount());
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
