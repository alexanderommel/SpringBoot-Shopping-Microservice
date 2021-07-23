package com.example.tongue.merchants.models;

public class ProductDTO {
    private Long id;
    private Double price;

    public ProductDTO(Long id,Double price) {
        this.id = id;
        this.price=price;
    }

    public Long getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }
}
