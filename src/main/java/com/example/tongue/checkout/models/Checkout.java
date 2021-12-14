package com.example.tongue.checkout.models;

import com.example.tongue.integrations.customers.Customer;
import com.example.tongue.core.domain.Position;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.integrations.payments.Payment;
import com.example.tongue.shopping.models.Cart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkout {
    @Id @GeneratedValue
    private Long id;

    @OneToOne
    @JsonIgnoreProperties("id")
    private Cart cart;

    @ManyToOne
    private Customer customer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="longitude",column = @Column(name="origin_longitude")),
            @AttributeOverride(name="latitude",column=@Column(name="origin_latitude")),
            @AttributeOverride(name = "address",column = @Column(name = "origin_address"))
    })
    private Position origin;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="longitude",column = @Column(name="destination_longitude")),
            @AttributeOverride(name="latitude",column=@Column(name="destination_latitude")),
            @AttributeOverride(name = "address",column = @Column(name = "destination_address"))
    })
    private Position destination;

    @ManyToOne
    @JsonIgnoreProperties({"name","location","representative",
    "representativePhone","plan","postalCode","currency_code",
    "allowCashPayments","enabledCurrencies","hasActiveDiscounts",
    "country_code","storeImageURL"})
    private StoreVariant storeVariant;

    @Embedded
    private Payment payment; //Cards have validation tokens

    private Instant created_at = Instant.now(); //ISO 8601;

    private CheckoutPrice price=new CheckoutPrice();

    private Instant FinishedAt;

    private Instant cancelledAt;

    private String sourceDevice;

    @JsonIgnore
    public void updateCheckout(){
        price.setCartTotal(cart.getPrice().getTotalPrice());
        price.setCartSubtotal(cart.getPrice().getFinalPrice());
        price.setCheckoutTotal(price.getCartTotal().add(price.getShippingTotal()));
        price.setCheckoutSubtotal(price.getCartSubtotal().add(price.getShippingSubtotal()));
    }

    public enum Status{

    }

}
