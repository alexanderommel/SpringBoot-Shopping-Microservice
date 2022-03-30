package com.example.tongue.services;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.integration.shipping.ShippingBroker;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.domain.shopping.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Component
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
    private ShippingBroker shippingBroker;


    public FlowMessage run(CheckoutAttribute checkoutAttribute, HttpSession session){
        FlowMessage response = new FlowMessage();
        response.setSolved(false);
        if (checkoutAttribute==null){
            response.setErrorMessage("Null CheckoutAttribute");
            response.setErrorStage("Init");
            return response;
        }else if (checkoutAttribute.getName()==null || checkoutAttribute.getAttribute()==null){
            response.setErrorMessage("Null CheckoutAttribute Field");
            response.setErrorStage("Init");
            return response;
        }
        Checkout checkout = checkoutSession.get(session);
        if (checkout==null){
            response.setErrorMessage("You must create a Checkout first");
            return response;
        }
        ValidationResponse validationResponse =checkoutValidation.attributeValidation(checkoutAttribute);
        if (!validationResponse.isSolved()){
            response.setErrorMessage(validationResponse.getErrorMessage());
            response.setErrorStage("Validation Error");
            return response;
        }
        checkout = addAttributeToCheckout(checkoutAttribute,checkout);
        checkout = addRealValuesToCheckout(checkoutAttribute,checkout);

        ShippingBrokerResponse brokerResponse =
                shippingBroker.requestShippingSummary(checkout.getShippingInfo().getCustomerPosition(),
                        checkout.getShippingInfo().getStorePosition());

        if (!brokerResponse.getIsSolved()){
            response.setErrorMessage(brokerResponse.getErrorMessage());
            return response;
        }
        Boolean successfulUpdate = checkout.getShoppingCart().updatePrice();
        if (!successfulUpdate){
            response.setErrorMessage("Error updating the price of the shopping shoppingCart, bad discount");
            response.setErrorStage("ShoppingCart Updating");
            return response;
        }
        checkout.updateCheckout();
        checkoutSession.save(checkout,session);
        response.setAttribute(checkout,"checkout");
        response.setSolved(true);
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
                Discount discount1 = item.getDiscount();
                if (discount1!=null){
                    discount1 = discountRepository.findById(discount1.getId()).get();
                    item.setDiscount(discount1);
                }
                List<Modifier> modifiers =item.getModifiers();
                List<Modifier> newModifiers = new ArrayList<>();
                if (!modifiers.isEmpty()){
                    for (Modifier modifier:modifiers) {
                        modifier = modifierRepository.findById(modifier.getId()).get();
                        newModifiers.add(modifier);
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
        if (attribute.getName()==CheckoutAttributeName.ORIGIN){
            Position origin = (Position) attribute.getAttribute();
            checkout.getShippingInfo().setCustomerPosition(origin);
        }
        if (attribute.getName()==CheckoutAttributeName.DESTINATION){
            Position destination = (Position) attribute.getAttribute();
            checkout.getShippingInfo().setStorePosition(destination);
        }
        return checkout;
    }

}
