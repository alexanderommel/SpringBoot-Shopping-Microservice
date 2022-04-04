package com.example.tongue.domain.checkout;

import com.example.tongue.integration.customers.Customer;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

/** Checkout is a process that has several stages that a customer follows in order**/

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("id")
    private ShoppingCart shoppingCart;

    @ManyToOne
    private Customer customer;

    @Embedded
    private ShippingInfo shippingInfo = new ShippingInfo();

    @ManyToOne
    @JsonIgnoreProperties({"name","location","representative",
    "representativePhone","plan","postalCode","currency_code",
    "allowCashPayments","enabledCurrencies","hasActiveDiscounts",
    "country_code","storeImageURL"})
    private StoreVariant storeVariant;

    @Embedded
    private PaymentInfo paymentInfo;

    private CheckoutPrice price=new CheckoutPrice();

    private Instant created_at; //ISO 8601;

    private Instant FinishedAt;

    private Instant expiresAt;

    private Instant cancelledAt;

    private String sourceDevice;

    private String resource;

    @JsonIgnore
    public void updateCheckout(){
        price.setShippingTotal(shippingInfo.getFee());
        price.setShippingSubtotal(shippingInfo.getFee());
        price.setCartTotal(shoppingCart.getPrice().getTotalPrice());
        price.setCartSubtotal(shoppingCart.getPrice().getFinalPrice());
        price.setCheckoutTotal(price.getCartTotal().add(price.getShippingTotal()));
        price.setCheckoutSubtotal(price.getCartSubtotal().add(price.getShippingSubtotal()));
    }

    public enum CheckoutStatus{
        CREATED, EXPIRED, PACKAGING_REQUEST, PACKAGING, PACKAGING_ACCEPTED, SHIPPING_REQUEST, SHIPPING_ACCEPTED, FINISHED, CANCELED
    }

}
