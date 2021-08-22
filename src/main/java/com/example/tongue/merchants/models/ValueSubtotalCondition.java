package com.example.tongue.merchants.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Embeddable
public class ValueSubtotalCondition {
    //FIELDS
    @Column(name = "value_leq")
    private BigDecimal leq;
    @Column(name = "value_heq")
    private BigDecimal heq;
    @Column(name = "value_eq")
    private BigDecimal eq;
    //METHODS
    public Boolean isAccomplishedOn(List<Product> cart){
        Double subtotal = getCartSubtotal(cart);
        if(leq !=null){
            if(subtotal> leq){
                return false;
            }
        }
        if(heq !=null){
            if(subtotal< heq){
                return false;
            }
        }
        if(eq !=null){
            if (subtotal!= eq){
                return false;
            }
        }
        return true;
    }
    private Double getCartSubtotal(List<Product> cart){
        Double subtotal=0.0;
        for (Product product:
             cart) {
            subtotal = subtotal + product.getPrice();
        }
        return subtotal;
    }

    public Double getLeq() {
        return leq;
    }

    public void setLeq(Double valueLeq) {
        this.leq = valueLeq;
    }

    public Double getHeq() {
        return heq;
    }

    public void setHeq(Double valueHeq) {
        this.heq = valueHeq;
    }

    public Double getEq() {
        return eq;
    }

    public void setEq(Double valueEq) {
        this.eq = valueEq;
    }

    @Override
    public String toString() {
        return "ValueSubtotalCondition{" +
                "valueLeq=" + Objects.toString(leq,"") +
                ", valueHeq=" + Objects.toString(heq,"") +
                ", valueEq=" + Objects.toString(eq,"") +
                '}';
    }
}
