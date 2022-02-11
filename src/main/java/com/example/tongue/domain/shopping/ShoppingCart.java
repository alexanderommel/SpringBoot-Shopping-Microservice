package com.example.tongue.domain.shopping;

import com.example.tongue.domain.merchant.enumerations.ValueType;
import com.example.tongue.domain.merchant.Discount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCart {

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
    public Boolean updatePrice(){
        if (discount!=null){
            if (!discount.validForCart(this))
                return false;
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
        }
        price.setFinalPrice(price.getTotalPrice().subtract(price.getDiscountedAmount()));
        return true;
    }

}
