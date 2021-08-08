package com.example.tongue.merchants.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class GroupModifier {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private StoreVariant storeVariant;

    @NotNull
    private GroupModifierType type;

    @NotNull
    private String context;

    // Useful when group type modifier is mandatory
    private int maximumActiveModifiers=1;
    private int minimumActiveModifiers=1;

    // Reserved for future implementations
    private String tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public StoreVariant getStoreVariant() {
        return storeVariant;
    }

    public void setStoreVariant(StoreVariant storeVariant) {
        this.storeVariant = storeVariant;
    }

    public GroupModifierType getType() {
        return type;
    }

    public void setType(GroupModifierType type) {
        this.type = type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getMaximumActiveModifiers() {
        return maximumActiveModifiers;
    }

    public void setMaximumActiveModifiers(int maximumActiveModifiers) {
        this.maximumActiveModifiers = maximumActiveModifiers;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getMinimumActiveModifiers() {
        return minimumActiveModifiers;
    }

    public void setMinimumActiveModifiers(int minimumActiveModifiers) {
        this.minimumActiveModifiers = minimumActiveModifiers;
    }
}
