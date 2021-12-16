package com.example.tongue.domain.merchant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class CollectionProductAllocation {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties({"storeVariant","title","tag",
    "imageUrl","status"})
    private Collection collection;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties({"adjustments","originalPrice","inventorId"})
    private Product product;

    // New products are allocated on the top
    private int position=-1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
