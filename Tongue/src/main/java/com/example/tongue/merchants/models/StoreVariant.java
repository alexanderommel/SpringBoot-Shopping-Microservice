package com.example.tongue.merchants.models;

import com.example.tongue.locations.models.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class StoreVariant {

    private @Id @GeneratedValue Long id;

    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Store store;

    @ManyToOne(optional=false, fetch = FetchType.EAGER)
    private Location location;

    private String representative;

    private String representativePhone;

    private String plan;

    private String postalCode;

    private String currency_code="USD";

    private Boolean allowCashPayments=true;

    private String enabledCurrencies;

    private Boolean hasActiveDiscounts=false; //Useful to avoid searching for discounts

    private String country_code="EC";

    private String storeImageURL;

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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getRepresentativePhone() {
        return representativePhone;
    }

    public void setRepresentativePhone(String representativePhone) {
        this.representativePhone = representativePhone;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public Boolean getAllowCashPayments() {
        return allowCashPayments;
    }

    public void setAllowCashPayments(Boolean allowCashPayments) {
        this.allowCashPayments = allowCashPayments;
    }

    public String getEnabledCurrencies() {
        return enabledCurrencies;
    }

    public void setEnabledCurrencies(String enabledCurrencies) {
        this.enabledCurrencies = enabledCurrencies;
    }

    public Boolean getHasActiveDiscounts() {
        return hasActiveDiscounts;
    }

    public void setHasActiveDiscounts(Boolean hasActiveDiscounts) {
        this.hasActiveDiscounts = hasActiveDiscounts;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getStoreImageURL() {
        return storeImageURL;
    }

    public void setStoreImageURL(String storeImageURL) {
        this.storeImageURL = storeImageURL;
    }
}
