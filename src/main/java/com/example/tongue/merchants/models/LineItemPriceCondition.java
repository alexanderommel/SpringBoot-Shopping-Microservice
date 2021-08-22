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
        if (leq!=null){
            if(product.getPrice()>leq){
                return false;
            }
        }
        if (heq!=null){
            if(product.getPrice()<heq){
                return false;
            }
        }
        if (eq!=null){
            if(product.getPrice()!=eq){
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
                if(product.getPrice()>leq){
                    continue;
                }
            }
            if (heq!=null){
                if(product.getPrice()<heq){
                    continue;
                }
            }
            if (eq!=null){
                if(product.getPrice()!=eq){
                    continue;
                }
            }
            validEntities.add(product);
        }
        return validEntities;
    }

    public Double getLeq() {
        return leq;
    }

    public void setLeq(Double leq) {
        this.leq = leq;
    }

    public Double getHeq() {
        return heq;
    }

    public void setHeq(Double heq) {
        this.heq = heq;
    }

    public Double getEq() {
        return eq;
    }

    public void setEq(Double eq) {
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
