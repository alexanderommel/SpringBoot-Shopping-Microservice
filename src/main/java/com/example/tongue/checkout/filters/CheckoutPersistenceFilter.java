package com.example.tongue.checkout.filters;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.sales.models.LineItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CheckoutPersistenceFilter implements CheckoutFilter {

    // Fields
    private CheckoutPersistenceAction persistenceAction;
    private
    StoreVariantRepository storeVariantRepository;
    private
    LocationRepository locationRepository;
    private
    ProductRepository productRepository;
    private
    DiscountRepository discountRepository;
    private
    ModifierRepository modifierRepository;
    private
    CheckoutRepository checkoutRepository;

    public CheckoutPersistenceFilter(CheckoutPersistenceAction persistenceAction,
                                     StoreVariantRepository storeVariantRepository,
                                     LocationRepository locationRepository,
                                     ProductRepository productRepository,
                                     DiscountRepository discountRepository,
                                     ModifierRepository modifierRepository,
                                     CheckoutRepository checkoutRepository){
        this.persistenceAction=persistenceAction;
        this.storeVariantRepository=storeVariantRepository;
        this.locationRepository=locationRepository;
        this.productRepository=productRepository;
        this.discountRepository=discountRepository;
        this.modifierRepository=modifierRepository;
        this.checkoutRepository=checkoutRepository;
    }

    @Override
    public Checkout doFilter(Checkout checkout, HttpSession session) {
        if (persistenceAction==CheckoutPersistenceAction.UPDATE){
            return updateCheckoutInternal(checkout);
        }

        return null;
    }

    private Checkout updateCheckoutInternal(Checkout checkout){
        // Update Shipping costs  by using Destination location and Store Variant location
        // This uses Shipping API to compute the total amount
        // First implementation doesn't support shipping discounts
        BigDecimal shippingRate = BigDecimal.valueOf(2.25); // temp
        checkout.getPrice().setShippingTotal(shippingRate);
        checkout.getPrice().setShippingSubtotal(shippingRate);

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i<checkout.getCart().getItems().size(); i++){
            Product product = checkout.getCart().getItems().get(i).getProduct();
            Optional<Product> p = productRepository.findById(product.getId());
            LineItem lineItem = checkout.getCart().getItems().get(i);
            lineItem.setProduct(p.get());
            //checkout.getCart().getItems().get(i).setProduct(p);
        }
        checkout.getCart().updatePrice();
        checkout.updateCheckout();
        // Cart price update is done by internal cart pricing update
        return checkout;
    }
}
