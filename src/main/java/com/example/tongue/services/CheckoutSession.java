package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.CheckoutPrice;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.CartPrice;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
public class CheckoutSession {

    private StoreVariantRepository storeVariantRepository;

    public CheckoutSession(@Autowired StoreVariantRepository storeVariantRepository){
        this.storeVariantRepository=storeVariantRepository;
    }

    public Checkout createCheckout(Checkout c, HttpSession session) throws Exception{
        log.info("Creating Checkout on Session");
        StoreVariant storeVariant =
                storeVariantRepository.findById(c.getStoreVariant().getId()).get();
        if (storeVariant==null)
            throw new NullPointerException("StoreVariant bound to Checkout has an invalid id");

        CheckoutPrice checkoutPrice = CheckoutPrice.builder()
                .checkoutSubtotal(BigDecimal.ZERO)
                .checkoutTotal(BigDecimal.ZERO)
                .cartSubtotal(BigDecimal.ZERO)
                .cartTotal(BigDecimal.ZERO)
                .shippingSubtotal(BigDecimal.ZERO)
                .shippingTotal(BigDecimal.ZERO)
                .build();

        CartPrice cartPrice = CartPrice.builder()
                .finalPrice(BigDecimal.ZERO)
                .totalPrice(BigDecimal.ZERO)
                .discountedAmount(BigDecimal.ZERO)
                .currency_code(storeVariant.getCurrency())
                .build();

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .paymentMethod(PaymentInfo.PaymentMethod.CASH)
                .build();

        c.setCreated_at(Instant.now());
        c.setStoreVariant(storeVariant);
        c.getShippingInfo().setStorePosition(storeVariant.getLocation());
        c.setPaymentInfo(paymentInfo);
        c.setExpiresAt(Instant.now().plusSeconds(3600));
        c.setSourceDevice("Customers-App");
        c.setPrice(checkoutPrice);
        c.getShoppingCart().setPrice(cartPrice);

        session.setAttribute("CHECKOUT",c);
        log.info("Checkout Created on Session successfully");
        return c;
    }

    public Checkout save(Checkout checkout, HttpSession session){
        log.info("Updating Checkout on Session...");
        Checkout c1 = (Checkout) session.getAttribute("CHECKOUT");
        StoreVariant storeVariant = c1.getStoreVariant();
        checkout.setStoreVariant(c1.getStoreVariant());
        log.info("Checkout updated successfully");
        session.setAttribute("CHECKOUT",checkout);
        return checkout;
    }

    public Checkout get(HttpSession session){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        return checkout;
    }

    public Checkout delete(HttpSession session){
        log.info("Removing Checkout from session");
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        session.removeAttribute("CHECKOUT");
        return checkout;
    }

}
