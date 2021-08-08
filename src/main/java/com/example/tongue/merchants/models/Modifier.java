package com.example.tongue.merchants.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Modifier {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private GroupModifier groupModifier;

    private BigDecimal price= BigDecimal.valueOf(0.0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupModifier getGroupModifier() {
        return groupModifier;
    }

    public void setGroupModifier(GroupModifier groupModifier) {
        this.groupModifier = groupModifier;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
