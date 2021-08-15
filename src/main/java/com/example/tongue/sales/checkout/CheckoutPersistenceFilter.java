package com.example.tongue.sales.checkout;

import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class CheckoutPersistenceFilter implements CheckoutFilter{

    // Fields
    private CheckoutPersistenceAction persistenceAction;
    private @Autowired
    StoreVariantRepository storeVariantRepository;
    private @Autowired
    LocationRepository locationRepository;
    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    DiscountRepository discountRepository;
    private @Autowired
    ModifierRepository modifierRepository;

    public CheckoutPersistenceFilter(CheckoutPersistenceAction persistenceAction){
        this.persistenceAction=persistenceAction;
    }

    @Override
    public Checkout doFilter(Checkout checkout) {
        return null;
    }

    private Checkout updateCheckoutInternal(Checkout checkout){
        // Update Shipping costs  by using Destination location and Store Variant location
        // This uses Shipping API to compute the total amount
        // First implementation doesn't support shipping discounts
        BigDecimal shippingRate = BigDecimal.valueOf(2.25); // temp
        checkout.getPrice().setShippingTotal(shippingRate);
        checkout.getPrice().setShippingSubtotal(shippingRate);
        // Cart price update is done by internal cart pricing update
        return checkout;
    }
}
