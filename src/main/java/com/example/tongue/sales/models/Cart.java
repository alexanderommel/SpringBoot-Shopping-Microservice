package com.example.tongue.sales.models;

import com.example.tongue.merchants.models.Discount;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Cart {

    private @Id @GeneratedValue Long id;

    @OneToMany
    private List<LineItem> items;

    private String instructions=null;

    @ManyToOne
    private Discount discount=null;

    @Embedded
    private CartPrice price;

    @JsonIgnore
    public void updatePrice(){
        if (discount!=null){
            Double cumulatedItemPriceWithDiscountsIgnored = 0.0;
            for (LineItem item: items){
                item.ignoreDiscountAndUpdatePrice();
                Double itemUnitPrice = item.getPrice().getFinalPrice();
                cumulatedItemPriceWithDiscountsIgnored =
                        cumulatedItemPriceWithDiscountsIgnored + itemUnitPrice;
            }
            price.setTotalPrice(cumulatedItemPriceWithDiscountsIgnored);
            String discountType = discount.getValueType();
            //fixed_amount,percentage
            if (discountType.equalsIgnoreCase("fixed")){
                price.setDiscountedAmount(discount.getValue());
            }else {
                Double percentage = discount.getValue();
                Double amountDiscounted = percentage*price.getTotalPrice();
                price.setDiscountedAmount(amountDiscounted);
            }
            price.setFinalPrice(price.getTotalPrice()-price.getDiscountedAmount());
        }else {
            Double cumulatedTotalItemPrice = 0.0;
            Double cumulatedDiscountedItemPrice = 0.0;
            for (LineItem item:items){
                item.updatePrice();
                Double itemTotalPrice = item.getPrice().getTotalPrice();
                Double itemDiscountedPrice = item.getPrice().getTotalDiscountedAmount();
                cumulatedTotalItemPrice =
                        cumulatedTotalItemPrice + itemTotalPrice;
                cumulatedDiscountedItemPrice =
                        cumulatedDiscountedItemPrice + itemDiscountedPrice;
            }
            price.setTotalPrice(cumulatedTotalItemPrice);
            price.setDiscountedAmount(cumulatedDiscountedItemPrice);
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
