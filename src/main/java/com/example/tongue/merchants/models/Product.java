package com.example.tongue.merchants.models;


import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
public class Product {

    //FIELDS
    private @Id @GeneratedValue Long id;


    @Size(max=200,
            message = "Description must be between 0 and 200 characters")
    private String description;


    @Size(max = 100, message = "Handle must be between 0 and 100 characters")
    @Pattern(regexp = "[a-zA-z0-9]{1,50}",
            message = "Pattern must contain only numbers and letters," +
            " also its size must be between 1 and 50")
    private String handle;


    @NotBlank(message = "Null value not admitted for title")
    @Size(max = 50, message = "Title must be between 0 and 50 characters")
    private String title;


    @Pattern(regexp = "[a-zA-Z0-9]{1,50}",
            message = "Type must be between 1 a 50 characters")
    private String type;


    @Pattern(regexp = "(active|draft|archived)",
            message = "Status must be active, draft or archived")
    private String status="active"; //active|draft|archived


    @Pattern(regexp = "([a-zA-Z0-9]{1,20},){0,10}[a-zA-Z0-9]{1,20}",
            message =
    "Tags must follow the next regular expression" +
            " ([a-zA-Z0-9]{1,20},){0,10}[a-zA-Z0-9]{1,20}")
    private String tags;



    //@NotBlank(message = "Store must not be empty")
    @ManyToOne(optional = false,
            fetch = FetchType.EAGER)
    @NotNull(message = "Store variant is mandatory")
    private StoreVariant storeVariant;


    private String inventorId; //identifier for vendor


    @NotNull(message = "Null value not admitted for Price")
    @Min(value = 0, message = "Price should be higher than zero")
    @Max(value = 1000, message = "Price must not be higher than 1000$")
    private Double price;


    @NotEmpty(message = "Currency code must not be empty")
    @Pattern(regexp = "(USD)") //ISO 4217
    @Column(name = "currency_code", updatable = false)
    private String currency_code="USD";


    @Column(name = "original_price")
    private Double originalPrice; //Price before adjustment or scaling


    private String adjustments; // {price:25.50,date:25-06-2021}



    //METHODS


    public StoreVariant getStoreVariant() {
        return storeVariant;
    }

    public void setStoreVariant(StoreVariant storeVariant) {
        this.storeVariant = storeVariant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInventorId() {
        return inventorId;
    }

    public void setInventorId(String inventorId) {
        this.inventorId = inventorId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(String adjustments) {
        this.adjustments = adjustments;
    }



    @Override
    public boolean equals(Object obj) {
        Product product = (Product) obj;
        return product.getId()==this.id;
    }
}
