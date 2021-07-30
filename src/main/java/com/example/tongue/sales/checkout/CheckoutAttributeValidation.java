package com.example.tongue.sales.checkout;

import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.payments.models.Payment;
import com.example.tongue.sales.models.Cart;
import com.example.tongue.sales.models.LineItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CheckoutAttributeValidation {

    private ProductRepository productRepository;
    private DiscountRepository discountRepository;

    public CheckoutAttributeValidation(@Autowired ProductRepository productRepository,
                                       @Autowired DiscountRepository discountRepository){
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    public Boolean supports(CheckoutAttribute checkoutAttribute){

    return true;
    }

    public Boolean validate(CheckoutAttribute checkoutAttribute){
        return true;
    }

    private Boolean validatePayment(Payment payment){
        return true;
    }

    private Boolean validateCart(Cart cart){
        return true;
    }

    private Boolean validateLineItems(List<LineItem> lineItems){
        return true;
    }

    private Boolean validateLineItem(LineItem lineItem){
        return true;
    }

    private Boolean validateDiscount(Discount discount){
        return true;
    }

    private Boolean validateLocation(Location location){
        return true;
    }
}
