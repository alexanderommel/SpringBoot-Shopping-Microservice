package com.example.tongue.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.CheckoutAttribute;
import com.example.tongue.domain.checkout.CheckoutAttributeName;
import com.example.tongue.domain.checkout.ValidationResponse;
import com.example.tongue.domain.checkout.Position;
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
        ValidationResponse response;
        response = validateAttributes(checkout);
        return response;
    }

    private ValidationResponse validateAttributes(Checkout checkout){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        ShoppingCart shoppingCart = checkout.getShoppingCart();
        Position destination = checkout.getShippingInfo().getStorePosition();
        Position origin = checkout.getShippingInfo().getCustomerPosition();
        response = attributeValidation(new CheckoutAttribute(shoppingCart,CheckoutAttributeName.CART));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(destination,CheckoutAttributeName.DESTINATION));
        if (!response.isSolved())
            return response;
        response = attributeValidation(new CheckoutAttribute(origin,CheckoutAttributeName.ORIGIN));
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

        if (checkoutAttribute.getName() == CheckoutAttributeName.DESTINATION) {
            response = validateDestinationAttribute(checkoutAttribute);
        }

        if (checkoutAttribute.getName() == CheckoutAttributeName.ORIGIN) {
            response = validateDestinationAttribute(checkoutAttribute);
        }
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
        response.setSolved(true);
        return response;
    }


    private ValidationResponse validateDestinationAttribute(CheckoutAttribute checkoutAttribute){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Position destination = (Position) checkoutAttribute.getAttribute();
        if (destination == null){
            response.setErrorMessage("Origin position object is mandatory");
            return response;
        }
        if (!destination.isValid()){
            response.setErrorMessage("Destination position object has no valid format");
            return response;
        }
        response.setSolved(true);
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
