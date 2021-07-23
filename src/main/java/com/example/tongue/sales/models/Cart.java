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

    private String instructions;

    @ManyToOne
    private Discount discount;

    @Embedded
    private CartPrice price;

    @JsonIgnore
    public void updatePrice(){
        for (LineItem item:
             items) {
            if (discount!=null){
                //discount.validforCart
                item.updatePrice();
                Double itemPrice = item.getPrice().getFinalPrice();

            }
        }
    }
}
