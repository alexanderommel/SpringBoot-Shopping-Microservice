package com.example.tongue.merchants.models;

import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Embeddable
public class ValueSubtotalCondition {

    @Column(name = "value_leq")
    private BigDecimal leq;
    @Column(name = "value_heq")
    private BigDecimal heq;
    @Column(name = "value_eq")
    private BigDecimal eq;

    public Boolean isAccomplishedOn(Cart cart){
        BigDecimal subtotal = getCartSubtotal(cart);
        if(leq !=null){
            int conditional_answer = subtotal.compareTo(leq);
            if(conditional_answer==1){
                return false;
            }
        }
        if(heq !=null){
            int conditional_answer = subtotal.compareTo(heq);
            if(conditional_answer==-1){
                return false;
            }
        }
        if(eq !=null){
            int conditional_answer = subtotal.compareTo(eq);
            if (conditional_answer!=0){
                return false;
            }
        }
        return true;
    }
    private BigDecimal getCartSubtotal(Cart cart){
        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineItem item:
             cart.getItems()) {
            subtotal = subtotal.add(item.getProduct().getPrice());
        }
        return subtotal;
    }

    public BigDecimal getLeq() {
        return leq;
    }

    public void setLeq(BigDecimal valueLeq) {
        this.leq = valueLeq;
    }

    public BigDecimal getHeq() {
        return heq;
    }

    public void setHeq(BigDecimal valueHeq) {
        this.heq = valueHeq;
    }

    public BigDecimal getEq() {
        return eq;
    }

    public void setEq(BigDecimal valueEq) {
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
