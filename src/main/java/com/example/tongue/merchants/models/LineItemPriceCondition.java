package com.example.tongue.merchants.models;

import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class LineItemPriceCondition {
    //FIELDS
    @Column(name = "price_leq")
    private BigDecimal leq;
    @Column(name = "price_heq")
    private BigDecimal heq;
    @Column(name = "price_eq")
    private BigDecimal eq;
    //METHODS
    public Boolean isAccomplishedOn(List<Product> targets){ //Subtotal
        Boolean response=true;
        for (Product product:
             targets) {
            if (accomplishedBy(product)==false){
                return false;
            }
        }
        return true;
    }
    public Boolean accomplishedBy(Product product){

        if (product.getPrice()==null)
            return false;
        if (BigDecimal.valueOf(1).compareTo(product.getPrice())!=-1)
            return false;

        if (leq!=null){
            int conditional_answer = product.getPrice().compareTo(leq);
            if(conditional_answer==1){
                return false;
            }
        }
        if (heq!=null){
            int conditional_answer = product.getPrice().compareTo(heq);
            if(conditional_answer==-1){
                return false;
            }
        }
        if (eq!=null){
            int conditional_answer = product.getPrice().compareTo(eq);
            if(conditional_answer!=0){
                return false;
            }
        }
        return true;
    }
    public List<Product> obtainValidEntities(List<Product> targets){ //line item
        List<Product> validEntities = new ArrayList<>();
        for (Product product:
                targets) {
            if (leq!=null){
                int conditional_answer = product.getPrice().compareTo(leq);
                if(conditional_answer==1){
                    continue;
                }
            }
            if (heq!=null){
                int conditional_answer = product.getPrice().compareTo(heq);
                if(conditional_answer==-1){
                    continue;
                }
            }
            if (eq!=null){
                int conditional_answer = product.getPrice().compareTo(eq);
                if(conditional_answer!=0){
                    continue;
                }
            }
            validEntities.add(product);
        }
        return validEntities;
    }

    public BigDecimal getLeq() {
        return leq;
    }

    public void setLeq(BigDecimal leq) {
        this.leq = leq;
    }

    public BigDecimal getHeq() {
        return heq;
    }

    public void setHeq(BigDecimal heq) {
        this.heq = heq;
    }

    public BigDecimal getEq() {
        return eq;
    }

    public void setEq(BigDecimal eq) {
        this.eq = eq;
    }

    @Override
    public String toString() {
        return "LineItemPriceCondition{" +
                "leq=" + Objects.toString(leq,"") +
                ", heq=" + Objects.toString(heq,"") +
                ", eq=" + Objects.toString(eq,"") +
                '}';
    }
}
