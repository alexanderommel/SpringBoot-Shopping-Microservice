package com.example.tongue.services;

import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.domain.shopping.LineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CheckoutValidation {


    private DiscountRepository discountRepository;
    private StoreVariantRepository storeVariantRepository;
    private ProductRepository productRepository;
    private ModifierRepository modifierRepository;

    public CheckoutValidation(@Autowired DiscountRepository discountRepository,
                              @Autowired StoreVariantRepository storeVariantRepository,
                              @Autowired ProductRepository productRepository,
                              @Autowired ModifierRepository modifierRepository){

        this.discountRepository=discountRepository;
        this.storeVariantRepository=storeVariantRepository;
        this.productRepository=productRepository;
        this.modifierRepository=modifierRepository;
    }

    public ValidationResponse hardValidation(Checkout checkout){
        log.info("Running hard validation...");
        ValidationResponse response;
        response = validateAttributes(checkout);
        if (!response.isSolved())
            return response;
        response = validateSources(checkout);
        log.info("Hard Validation finished");
        return response;
    }

    private ValidationResponse validateSources(Checkout c){
        log.info("Validating sources...");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Long id = c.getStoreVariant().getId();
        ShoppingCart shoppingCart = c.getShoppingCart();
        List<LineItem> lineItems = shoppingCart.getItems();
        Discount cartLevelDiscount =  shoppingCart.getDiscount();

        if (cartLevelDiscount!=null && cartLevelDiscount.getStoreVariant()!=null){
            Boolean discountTest = cartLevelDiscount.getStoreVariant().getId()==id;
            if (discountTest==false){
                response.setErrorMessage("CartLevelDiscount doesn't belong to the StoreVariant");
                return response;
            }
        }

        for (LineItem l:lineItems
             ) {
            Product p = l.getProduct();
            if (p.getStoreVariant().getId()!=id){
                response.setErrorMessage("Product with id '"+p.getId()+"' doesn't belong to the StoreVariant");
                return response;
            }
            if (l.getDiscount()!=null){
                if (l.getDiscount().getStoreVariant().getId()!=id){
                    response.setErrorMessage("Discount with id '"+l.getDiscount().getId()+"' doesn't belong to the StoreVariant");
                    return response;
                }
            }
            for (Modifier m:l.getModifiers()
                 ) {
                if (m.getGroupModifier().getProduct().getId()!=p.getId()){
                    response.setErrorMessage("Modifier with id '"+m.getId()+"' is not a modifier of product with id '"+p.getId());
                    return response;
                }
            }
        }

        log.info("Sources validation successfully");
        response.setSolved(true);
        return response;
    }

    private ValidationResponse validateAttributes(Checkout checkout){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        ShoppingCart shoppingCart = checkout.getShoppingCart();
        ShippingInfo shippingInfo = checkout.getShippingInfo();
        PaymentInfo paymentInfo = checkout.getPaymentInfo();
        response = attributeValidation(new CheckoutAttribute(shoppingCart,CheckoutAttributeName.CART));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(shippingInfo,CheckoutAttributeName.SHIPPING));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(paymentInfo,CheckoutAttributeName.PAYMENT));
        return response;
    }

    /** Attribute Validation validates that everything that's in the shopping shoppingCart has a real id **/
    public ValidationResponse attributeValidation(CheckoutAttribute checkoutAttribute) {
        log.info("Running attribute validation");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        if (checkoutAttribute == null) {
            response.setErrorMessage("CheckoutAttribute instance is empty");
        }
        if (checkoutAttribute.getAttribute()==null){
            response.setErrorMessage("CheckoutAttribute Attribute field is empty");
            return response;
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.CART) {
            response = validateCartAttribute(checkoutAttribute);
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.SHIPPING) {
            response = validateShippingInfoAttribute(checkoutAttribute);
        }
        if (checkoutAttribute.getName() == CheckoutAttributeName.PAYMENT){
            response = validatePaymentInfoAttribute(checkoutAttribute);
        }
        log.info("Attribute validation finished");
        return response;
    }

    public ValidationResponse softValidation(Checkout checkout){
        log.info("Running Soft Validation");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Position origin = checkout.getShippingInfo().getCustomerPosition();

        if (origin==null){
            log.info("Customer position (origin) shouldn't be empty");
            response.setErrorMessage("Origin position object is mandatory");
            return response;
        }

        Float latitude = origin.getLatitude();
        Float longitude = origin.getLongitude();
        if(latitude==null || longitude==null){
            response.setErrorMessage("Origin position attributes must be populated");
            return response;
        }

        StoreVariant storeVariant = checkout.getStoreVariant();
        if (storeVariant==null){
            response.setErrorMessage("StoreVariant object is mandatory");
            return response;
        }
        Long storeVariantId = storeVariant.getId();
        if (storeVariantId==null){
            response.setErrorMessage("Store variant field 'id' is mandatory");
            return response;
        }
        if (!storeVariantRepository.existsById(storeVariantId)){
            response.setErrorMessage("No such Store Variant");
            return response;
        }
        try{
            ShoppingCart shoppingCart = checkout.getShoppingCart();
            LineItem item = shoppingCart.getItems().get(0);
            Product product = item.getProduct();
            if (!productRepository.existsById(product.getId())){
                response.setErrorMessage("No such Product with id '"+product.getId()+"'");
                return response;
            }
        }catch (Exception e){
            response.setErrorMessage("Please add at least one item to your Checkout instance");
            return response;
        }
        log.info("Checkout is valid");
        response.setSolved(true);
        return response;
    }

    private ValidationResponse validatePaymentInfoAttribute(CheckoutAttribute checkoutAttribute){
        log.info("Validating PaymentInfo Attribute");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        PaymentInfo paymentInfo = (PaymentInfo) checkoutAttribute.getAttribute();
        if (paymentInfo==null){
            response.setErrorMessage("PaymentInfo attribute is empty!");
            return response;
        }
        if (paymentInfo.getPaymentMethod()==null){
            response.setErrorMessage("PaymentMethod is empty!");
            return response;
        }
        if (paymentInfo.getPaymentSession()==null){
            response.setErrorMessage("PaymentSession id is empty!");
            return response;
        }
        log.info("Payment Info Validation status is OK");
        response.setSolved(true);
        return response;
    }


    private ValidationResponse validateShippingInfoAttribute(CheckoutAttribute checkoutAttribute){
        log.info("Validating Shipping Info Attribute");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        ShippingInfo shippingInfo = (ShippingInfo) checkoutAttribute.getAttribute();
        if (shippingInfo==null){
            response.setErrorMessage("ShippingInfo attribute is empty!");
            return response;
        }
        if (shippingInfo.getCustomerPosition() == null){
            response.setErrorMessage("Customer position object is mandatory");
            return response;
        }
        if (!shippingInfo.getCustomerPosition().isValid()){
            response.setErrorMessage("Customer position object has no valid format");
            return response;
        }
        if (shippingInfo.getFee()==null){
            response.setErrorMessage("Shipping Fee is empty!");
            return response;
        }
        if (shippingInfo.getShippingSession()==null){
            response.setErrorMessage("Shipping Session id is empty!");
            return response;
        }
        response.setSolved(true);
        log.info("Shipping Info Validation status is OK");
        return response;
    }

    /** It verifies that everything in the shoppingCart has an existing associated id (Except store variant)**/
    private ValidationResponse validateCartAttribute(CheckoutAttribute attribute) {
        log.info("Validating Cart Attribute");
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        ShoppingCart shoppingCart = (ShoppingCart) attribute.getAttribute();
        Discount discount = shoppingCart.getDiscount();
        List<LineItem> items = shoppingCart.getItems();
        if (discount != null) {

            response = discountValidationWrapper(discount, response);
            if (!response.isSolved())
                return response;
            response.setSolved(false);

        }
        if (items != null) {
            if (items.isEmpty()){
                response.setErrorMessage("Your ShoppingCart shouldn't be empty");
                return response;
            }
            for (LineItem item : items) {
                Long productId = item.getProduct().getId();
                List<Modifier> modifiers = item.getModifiers();
                if (!productRepository.existsById(productId)) {
                    response.setErrorMessage("Product with id '" + productId + "' not found");
                    return response;
                }
                // Item Discount Validation
                Discount itemDiscount = item.getDiscount();
                if (!(itemDiscount == null)) {

                    response = discountValidationWrapper(discount, response);
                    if (!response.isSolved())
                        return response;
                    response.setSolved(false);

                }
                // Modifiers Validation
                if (modifiers != null) {
                    for (Modifier modifier : modifiers
                    ) {
                        Long modifierId = modifier.getId();
                        if (!modifierRepository.existsById(modifierId)) {
                            response.setErrorMessage("Modifier with id '" + modifierId + "' not found");
                            return response;
                        }
                    }
                }
            }
        }
        log.info("Shopping Cart Validation status is OK");
        response.setSolved(true);
        return response;
    }

    private ValidationResponse discountValidationWrapper(Discount discount,ValidationResponse response){
        Long discountId = discount.getId();
        response.setSolved(true);
        if (discountId==null){
            response.setErrorMessage("Empty Id reference");
            response.setSolved(false);
            return response;
        }
        if (!discountRepository.existsById(discountId)){
            response.setErrorMessage("No such discount with id '"+discountId+"'");
            response.setSolved(false);
        }
        return response;
    }

}
