package com.example.tongue.sales.checkout;

import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Modifier;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.sales.models.Cart;
import com.example.tongue.sales.models.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.Filter;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.List;

public class CheckoutRequestValidationFilter implements CheckoutFilter{

    private CheckoutValidationType validationType;
    private CheckoutAttribute checkoutAttribute;
    private @Autowired
    StoreVariantRepository storeVariantRepository;
    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    DiscountRepository discountRepository;

    public CheckoutRequestValidationFilter(CheckoutValidationType validationType){
        this.validationType=validationType;
    }

    public void setCheckoutAttribute(CheckoutAttribute checkoutAttribute) {
        this.checkoutAttribute = checkoutAttribute;
    }

    @Override
    public Checkout doFilter(Checkout checkout) {
        if (validationType==CheckoutValidationType.SIMPLE){
            validateSenderCheckout(checkout);
        }
        if (validationType==CheckoutValidationType.ATTRIBUTE){
            validateCheckoutAttribute();
        }
        return checkout;
    }

    private void modifierValidation(){
        // Modifier must share Store Variant with Checkout
    }

    // Validate CheckoutAttribute
    private void validateCheckoutAttribute(){
        if (!(checkoutAttribute ==null)){
            if (checkoutAttribute.getName()==CheckoutAttributeName.CART){
                Cart cart = (Cart) checkoutAttribute.getAttribute();
                Discount discount = cart.getDiscount();
                List<LineItem> items = cart.getItems();
                if (discount!=null){
                    if (!discountRepository.existsById(discount.getId()))
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No such discount with id '"+discount.getId()+"'");
                }
                if (items!=null){
                    for (LineItem item:items) {
                        // Product validation
                        if (productRepository.existsById(item.getProduct().getId()))
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "No such product with id '"+item.getProduct().getId()+"'");
                        // Discount validation
                        Discount itemDiscount = item.getDiscount();
                        if (itemDiscount!=null){
                            if (!discountRepository.existsById(itemDiscount.getId()))
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "No such discount with id '"+itemDiscount.getId()+"'");
                        }
                        // Modifiers Validation
                        List<Modifier> modifiers = item.getModifiers();
                        if (modifiers!=null){

                        }

                    }
                }
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.DESTINATION){
                // Not supported yet
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.ORIGIN){
                Location origin = (Location) checkoutAttribute.getAttribute();
                if (origin==null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Origin location object is mandatory");
                Boolean validLocationFormat = origin.validate(); // Not implemented yet
                if (validLocationFormat==false)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Origin location object has no valid format");
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.PAYMENT){
                // Future implementation
            }
        }
    }

    // Here we validate if customer has provided a properly populated minimum checkout
    // Minimum is: Origin, Store Variant and Cart with one Line Item (Only product is considered)
    private void validateSenderCheckout(Checkout checkout){
        Location origin = checkout.getOrigin();
        if (origin==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Origin location object is mandatory");
        Boolean validLocationFormat = origin.validate(); // Not implemented yet
        if (validLocationFormat==false)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Origin location object has no valid format");
        StoreVariant storeVariant = checkout.getStoreVariant();
        if (storeVariant==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "StoreVariant object is mandatory");
        Long storeVariantId = storeVariant.getId();
        if (storeVariantId==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Store variant field 'id' is mandatory");
        if (!storeVariantRepository.existsById(storeVariantId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No such Store Variant");
        try {
            Cart cart = checkout.getCart();
            LineItem item = cart.getItems().get(0);
            Product product = item.getProduct();
            if (!productRepository.existsById(product.getId()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No such line item product");
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Provide at least one valid Line Item");
        }
    }

}
