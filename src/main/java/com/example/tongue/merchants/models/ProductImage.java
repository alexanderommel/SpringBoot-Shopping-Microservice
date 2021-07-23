package com.example.tongue.merchants.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.URL;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Positive;

@Entity
public class ProductImage {

    //FIELDS
    @Id @GeneratedValue
    private Long id;


    @ManyToOne(optional = false/*, fetch = FetchType.EAGER*/)
    private Product product;


    private int priority=1;

    @NotNull //@URL
    private String source;


    @Positive
    private int width=100;


    @Positive
    private int height=100;

    @URL
    private String icon; //Necessary to reduce overload on client


    //METHODS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NotNull
    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public void setProduct(@NotNull Product product) {
        this.product = product;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    public String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
