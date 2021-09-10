package com.example.tongue.checkout.models;

import com.example.tongue.customers.models.Customer;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.payments.models.Payment;
import com.example.tongue.sales.models.Cart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Checkout {
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JsonIgnoreProperties("id")
    private Cart cart;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    @JsonIgnoreProperties("id")
    private Location origin;

    @ManyToOne
    @JsonIgnoreProperties("id")
    private Location destination;

    @ManyToOne
    @JsonIgnoreProperties({"name","location","representative",
    "representativePhone","plan","postalCode","currency_code",
    "allowCashPayments","enabledCurrencies","hasActiveDiscounts",
    "country_code","storeImageURL"})
    private StoreVariant storeVariant;

    @OneToOne
    private Payment payment; //Cards have validation tokens

    private String JSESSIONID;

    private Instant created_at = Instant.now(); //ISO 8601;

    private CheckoutPrice price;

    private Instant FinishedAt;

    private Instant cancelledAt;

    private String sourceDevice;

    private String sourceURL;

    private String currency_code="USD";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public StoreVariant getStoreVariant() {
        return storeVariant;
    }

    public void setStoreVariant(StoreVariant storeVariant) {
        this.storeVariant = storeVariant;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getJSESSIONID() {
        return JSESSIONID;
    }

    public void setJSESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public CheckoutPrice getPrice() {
        return price;
    }

    public void setPrice(CheckoutPrice price) {
        this.price = price;
    }

    public Instant getFinishedAt() {
        return FinishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        FinishedAt = finishedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(String sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    @JsonIgnore
    public void updateCheckout(){
        price.setCartTotal(cart.getPrice().getTotalPrice());
        price.setCartSubtotal(cart.getPrice().getFinalPrice());
    }
}
