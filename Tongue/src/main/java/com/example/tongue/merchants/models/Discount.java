package com.example.tongue.merchants.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;


@Entity
public class Discount {

    @Id @GeneratedValue
    private Long id;


    @Size(max = 300, message = "Description must be between 0 and 300 characters")
    private String description;


    @Size(max = 25, message = "Code must be between 0 and 25 characters")
    @Pattern(regexp = "[a-zA-Z0-9]{1,25}", message = "Code must contain only numbers and characters")
    private String code;


    private Boolean autoApplicable=false;


    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now(); //ISO 8601


    @Column(name = "expires_at", updatable = false)
    @Future(message = "Expire date must be after the current date")
    @NotNull(message = "Expire date field must not be empty")
    private Instant expiresAt;


    @Positive(message = "Usage limit must be higher than 0")
    private int usageLimit=1; //limit of application


    @NotNull(message = "Discount scope field must not be empty")
    @Pattern(regexp = "(line_items|subtotal)",
            message = "Discount scope should be one of these values (line_items|subtotal)")
    private String discountScope;


    @NotNull(message = "Product scope field must not be empty")
    @Pattern(regexp = "(all|entitled_only)",
            message = "Product scope should be one of these values (all|entitled_only)")
    private String productsScope; //{all,entitled_only}


    @NotNull(message = "Customer scope field must not be empty")
    @Pattern(regexp = "(all|entitled_only)",
            message = "Customer scope should be one of these values (all|entitled_only)")
    private String customersScope; //{all,entitled_only}


    @ManyToMany
    @UniqueElements(message = "Excluded products must be unique")
    private List<Product> excludedProducts;


    @NotNull(message = "Value type field must not be empty")
    @Pattern(regexp = "(fixed_amount|percentage)",
            message = "Value type should be one of these values (fixed_amount|percentage)")
    private String valueType; //{fixed_amount,percentage} //VALUE TYPE


    @NotNull(message = "Value field must not be empty")
    @Range(min = 0,max = 100,message = "Value should be between 0 and 100")
    private Double value; //{0-100 for percentage and R for fixed amounts}


    @NotNull(message = "Discount type field must not be empty")
    @Pattern(regexp = "(shipping|product)",
            message = "Value type should be one of these values (shipping|product)")
    private String discountType;


    @Positive(message = "Maximum amount must be higher than zero")
    private Double maximumAmount; //Used when discountType=percentage


    @Embedded
    private LineItemPriceCondition lineItemPriceCondition=null;


    @Embedded
    private ValueSubtotalCondition valueSubtotalCondition=null;


    @ManyToMany
    @UniqueElements(message = "Entitled products must be unique")
    private List<Product> entitledProducts=null;


    private String entitledCustomers;


    @Positive(message = "Priority value must be higher than zero")
    private int priority=1;


    private String requiredCompanions;


    //METHODS

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getAutoApplicable() {
        return autoApplicable;
    }

    public void setAutoApplicable(Boolean autoApplicable) {
        this.autoApplicable = autoApplicable;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public String getDiscountScope() {
        return discountScope;
    }

    public void setDiscountScope(String discountScope) {
        this.discountScope = discountScope;
    }

    public String getProductsScope() {
        return productsScope;
    }

    public void setProductsScope(String productsScope) {
        this.productsScope = productsScope;
    }

    public String getCustomersScope() {
        return customersScope;
    }

    public void setCustomersScope(String customersScope) {
        this.customersScope = customersScope;
    }

    @JsonIgnoreProperties(value = {"adjustments","description","handle","type","status","tags","vendor","inventorId","originalPrice"})
    public List<Product> getExcludedProducts() {
        return excludedProducts;
    }

    public void setExcludedProducts(List<Product> excludedProducts) {
        this.excludedProducts = excludedProducts;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(Double maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    //@JsonIgnore
    public LineItemPriceCondition getLineItemPriceCondition() {
        return lineItemPriceCondition;
    }

    public void setLineItemPriceCondition(LineItemPriceCondition lineItemPriceCondition) {
        this.lineItemPriceCondition = lineItemPriceCondition;
    }

    //@JsonIgnore
    public ValueSubtotalCondition getValueSubtotalCondition() {
        return valueSubtotalCondition;
    }

    public void setValueSubtotalCondition(ValueSubtotalCondition valueSubtotalCondition) {
        this.valueSubtotalCondition = valueSubtotalCondition;
    }

    @JsonIgnoreProperties(value = {"adjustments","description","handle","type","status","tags","vendor","inventorId","originalPrice"})
    public List<Product> getEntitledProducts() {
        return entitledProducts;
    }

    public void setEntitledProducts(List<Product> entitledProducts) {
        this.entitledProducts = entitledProducts;
    }

    public String getEntitledCustomers() {
        return entitledCustomers;
    }

    public void setEntitledCustomers(String entitledCustomers) {
        this.entitledCustomers = entitledCustomers;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRequiredCompanions() {
        return requiredCompanions;
    }

    public void setRequiredCompanions(String requiredCompanions) {
        this.requiredCompanions = requiredCompanions;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", discountScope='" + discountScope + '\'' +
                ", value='" + value + '\'' +
                ", productsScope='" + productsScope + '\'' +
                ", lineItemPriceCondition=" + Objects.toString(lineItemPriceCondition,"") +
                ", valueSubtotalCondition=" + Objects.toString(valueSubtotalCondition,"") +
                ", entitledProducts=" + Objects.toString(entitledProducts,"") +
                '}';
    }

    //METHODS

    @JsonIgnore
    public Boolean validForCart(Discount discount,List<Product> cart){
        /*
        Value Subtotal Condition
        */
        ValueSubtotalCondition subtotalCondition = discount.getValueSubtotalCondition();
        if (subtotalCondition!=null){
            if (!subtotalCondition.isAccomplishedOn(cart)){
                return false;
            }
        }
        /*
        Entitled validation for every product
         */
        if (discount.getDiscountScope().equalsIgnoreCase("subtotal")){
            for (Product product:
                    cart) {
                if (!validForProduct(discount,product)){
                    return false;
                }
            }
        }
        return true;
    }

    @JsonIgnore
    public Boolean validForProduct(Discount discount,Product product){
        if (discount.getProductsScope().equalsIgnoreCase("entitled_only")){
            List<Product> entitled = discount.getEntitledProducts();
            if (!entitled.contains(product)){
                return false;
            }
        }
        if (discount.getProductsScope().equalsIgnoreCase("all")){
            List<Product> excluded = discount.getExcludedProducts();
            if (excluded!=null){
                if (excluded.contains(product)){
                    return false;
                }
            }
        }
        LineItemPriceCondition condition = discount.getLineItemPriceCondition();
        if (condition!=null){
            if (!condition.accomplishedBy(product)){
                return false;
            }
        }
        return true;
    }

    /*
    Discount validation on its pure form is just about validate that the discount has not expired,
    verify that attribute values have been populated properly.
     */

    @JsonIgnore
    public Boolean isValid(){
        if (createdAt.compareTo(expiresAt)>=0){
            return false;
        }
        Instant currentTime = Instant.now();
        if(currentTime.compareTo(expiresAt)>0){
            return false; //discount has expired
        }
        return true;
    }

    /*
    Customer validation consists in verify that the customer has not used the discount yet, for this
    you can query a customer-discount table. Another validation consists in check in the entitledCustomers
    list for any matching.
     */

    @JsonIgnore
    public Boolean isValidForCustomer(){
        return true;
    }

    /*
    private Date getISODateFromString(String date){
        TemporalAccessor accessor = DateTimeFormatter.ISO_DATE_TIME.parse(date);
        Instant instant = Instant.from(accessor);
        instant.
    }

     */
}
