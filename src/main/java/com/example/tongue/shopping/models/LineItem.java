package com.example.tongue.shopping.models;

import com.example.tongue.merchants.enumerations.DiscountValueType;
import com.example.tongue.merchants.enumerations.ValueType;
import com.example.tongue.merchants.models.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @JsonIgnore
    public void addModifier(Modifier modifier){
        modifiers.add(modifier);
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
        if (modifiers!=null){
            if (!this.modifiers.isEmpty()){
                total =modifiers.stream().map(x -> x.getPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);
            }
        }
        return total;
    }

}
