package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRequestChain {

    private List<CheckoutFilter> filters;

    public CheckoutRequestChain(StoreVariantRepository storeVariantRepository,
                                ProductRepository productRepository,
                                ModifierRepository modifierRepository,
                                DiscountRepository discountRepository){
        this.filters = new ArrayList<>();
        CheckoutValidationFilter validationFilter = new CheckoutValidationFilter(storeVariantRepository,
                productRepository,
                discountRepository,
                modifierRepository,
                CheckoutValidationType.SIMPLE);
        CheckoutSessionFilter sessionFilter = new CheckoutSessionFilter();
        filters.add(validationFilter);
        filters.add(sessionFilter);
    }


    public Checkout doFilter(Checkout checkout, HttpSession httpSession){
        for (CheckoutFilter filter:filters) {
            checkout = filter.doFilter(checkout,httpSession);
        }
        return checkout;
    }
}
