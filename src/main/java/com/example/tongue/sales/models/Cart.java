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
        for (LineItem item:
             items) {
            if (discount!=null){
                //discount.validforCart
                item.updatePrice();
                Double itemPrice = item.getPrice().getFinalPrice();

            }
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
