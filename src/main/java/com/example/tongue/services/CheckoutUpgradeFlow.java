package com.example.tongue.services;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.shopping.LineItemPrice;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.integration.shipping.ShippingServiceBroker;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.domain.shopping.LineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CheckoutUpgradeFlow {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private ModifierRepository modifierRepository;
    @Autowired
    private CheckoutValidation checkoutValidation;
    @Autowired
    private CheckoutSession checkoutSession;
    @Autowired
    private ShippingServiceBroker shippingBroker;


    public FlowMessage run(CheckoutAttribute checkoutAttribute, HttpSession session){
        log.info("Checkout Upgrade Flow running...");
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        if (checkoutAttribute==null){
            log.info("CheckoutAttribute should be null!");
            response.setErrorMessage("Null CheckoutAttribute");
            return response;
        }else if (checkoutAttribute.getName()==null || checkoutAttribute.getAttribute()==null){
            response.setErrorMessage("Null CheckoutAttribute Field");
            return response;
        }
        Checkout checkout = checkoutSession.get(session);
        if (checkout==null){
            response.setErrorMessage("You must create a Checkout first");
            return response;
        }
        ValidationResponse validationResponse =checkoutValidation.attributeValidation(checkoutAttribute);
        if (!validationResponse.isSolved()){
            log.info("Validation Error->"+validationResponse.getErrorMessage());
            response.setErrorMessage(validationResponse.getErrorMessage());
            return response;
        }
        log.info("Validation passed");
        checkout = addAttributeToCheckout(checkoutAttribute,checkout);
        checkout = addRealValuesToCheckout(checkoutAttribute,checkout);

        if (checkoutAttribute.getName()==CheckoutAttributeName.CART){
            log.info("Updating Shopping Cart prices");
            Boolean successfulUpdate = checkout.getShoppingCart().updatePrice();
            if (!successfulUpdate){
                response.setErrorMessage("Error updating the price of the shopping shoppingCart");
                return response;
            }
        }

        log.info("Updating Checkout Prices");
        checkout.updateCheckout();
        checkout = checkoutSession.save(checkout,session);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
        log.info("Attribute updated successfully");
        return response;
    }

    /** After a successful checkout attribute validation, this method queries the repositories
     * to get the full values of everything inside the CheckoutAttribute**/
    private Checkout addRealValuesToCheckout(CheckoutAttribute checkoutAttribute,Checkout checkout){
        if (checkoutAttribute==null)
            return checkout;
        if (checkoutAttribute.getName()== CheckoutAttributeName.CART){
            ShoppingCart shoppingCart = (ShoppingCart) checkoutAttribute.getAttribute();
            Discount discount = shoppingCart.getDiscount();
            List<LineItem> itemList = shoppingCart.getItems();
            if (discount!=null){
                discount = discountRepository.findById(discount.getId()).get();
                shoppingCart.setDiscount(discount);
            }
            List<LineItem> newItems = new ArrayList<>();
            for (LineItem item:itemList) {
                Product product = item.getProduct();
                product = productRepository.findById(product.getId()).get();
                item.setProduct(product);
                item.setPrice(new LineItemPrice());
                Discount discount1 = item.getDiscount();
                if (discount1!=null){
                    discount1 = discountRepository.findById(discount1.getId()).get();
                    item.setDiscount(discount1);
                }
                List<Modifier> modifiers =item.getModifiers();
                List<Modifier> newModifiers = new ArrayList<>();
                if (modifiers!=null){
                    if (!modifiers.isEmpty()){
                        for (Modifier modifier:modifiers) {
                            modifier = modifierRepository.findById(modifier.getId()).get();
                            newModifiers.add(modifier);
                        }
                    }
                }
                item.setModifiers(newModifiers);
                newItems.add(item);
            }
            shoppingCart.setItems(newItems);
            checkout.setShoppingCart(shoppingCart);
        }
        return checkout;
    }

    private Checkout addAttributeToCheckout(CheckoutAttribute attribute,Checkout checkout){
        if (attribute==null)
            return checkout;
        if (attribute.getName()==CheckoutAttributeName.CART){
            ShoppingCart shoppingCart = (ShoppingCart) attribute.getAttribute();
            checkout.setShoppingCart(shoppingCart);
        }
        if (attribute.getName()==CheckoutAttributeName.SHIPPING){
            ShippingInfo shippingInfo = (ShippingInfo) attribute.getAttribute();
            shippingInfo.setStorePosition(checkout.getShippingInfo().getStorePosition());
            checkout.setShippingInfo(shippingInfo);
        }
        if (attribute.getName()==CheckoutAttributeName.PAYMENT){
            PaymentInfo paymentInfo = (PaymentInfo) attribute.getAttribute();
            checkout.setPaymentInfo(paymentInfo);
        }
        return checkout;
    }

}
