package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutAttribute;
import com.example.tongue.checkout.models.CheckoutAttributeName;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Modifier;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

public class CheckoutValidationFilter implements CheckoutFilter {

    private CheckoutValidationType validationType;
    private CheckoutAttribute checkoutAttribute;
    private
    StoreVariantRepository storeVariantRepository;
    private
    ProductRepository productRepository;
    private
    DiscountRepository discountRepository;
    private
    ModifierRepository modifierRepository;

    public CheckoutValidationFilter(StoreVariantRepository storeVariantRepository,
            ProductRepository productRepository,
            DiscountRepository discountRepository,
            ModifierRepository modifierRepository
            ,CheckoutValidationType validationType){

        this.storeVariantRepository=storeVariantRepository;
        this.productRepository=productRepository;
        this.discountRepository=discountRepository;
        this.modifierRepository=modifierRepository;
        this.validationType=validationType;
    }

    public CheckoutValidationFilter(CheckoutAttribute checkoutAttribute){
        this.validationType = CheckoutValidationType.ATTRIBUTE;
        this.checkoutAttribute = checkoutAttribute;
    }

    public void setCheckoutAttribute(CheckoutAttribute checkoutAttribute) {
        this.checkoutAttribute = checkoutAttribute;
    }

    @Override
    public Checkout doFilter(Checkout checkout, HttpSession session) {
        if (validationType==CheckoutValidationType.SIMPLE){
            validateSenderCheckout(checkout);
        }
        if (validationType==CheckoutValidationType.ATTRIBUTE){
            validateCheckoutAttribute(this.checkoutAttribute);
            if (checkoutAttribute.getName()== CheckoutAttributeName.CART)
                checkout.setCart((Cart) checkoutAttribute.getAttribute());
            else if (checkoutAttribute.getName()==CheckoutAttributeName.ORIGIN)
                checkout.setOrigin((Location) checkoutAttribute.getAttribute());
            else if (checkoutAttribute.getName()==CheckoutAttributeName.DESTINATION)
                checkout.setDestination((Location) checkoutAttribute.getAttribute());
        }
        return checkout;
    }

    private Boolean hasCheckoutAttributeNested(){
        return this.checkoutAttribute==null;
    }
    // Validate CheckoutAttribute
    // Validate Existence and Internal Rules
    private void validateCheckoutAttribute(CheckoutAttribute checkoutAttribute){
        if (hasCheckoutAttributeNested())
            throw new NullPointerException("Checkout Attribute shouldn't be null for checkout attribute validation");
        if (!(checkoutAttribute ==null)){
            if (checkoutAttribute.getName()==CheckoutAttributeName.CART){
                Cart cart = (Cart) checkoutAttribute.getAttribute();
                Discount discount = cart.getDiscount();
                List<LineItem> items = cart.getItems();
                if (discount!=null){
                    // Existence validation
                    if (!discountRepository.existsById(discount.getId()))
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No such discount with id '"+discount.getId()+"'");
                    // Internal rules
                    Optional<Discount> discount1 = discountRepository.findById(discount.getId());
                    if (!discount1.isEmpty()){
                        if (!discount1.get().isValid())
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Discount with 'id' has expired");
                    }
                }
                if (items!=null){
                    for (LineItem item:items) {
                        // Product validation
                        if (!productRepository.existsById(item.getProduct().getId()))
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "No such product with id '"+item.getProduct().getId()+"'");
                        // Discount validation
                        Discount itemDiscount = item.getDiscount();
                        if (itemDiscount!=null){
                            // Existence
                            if (!discountRepository.existsById(itemDiscount.getId()))
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "No such discount with id '"+itemDiscount.getId()+"'");
                            // Internal rules
                            Optional<Discount> discount1 = discountRepository.findById(itemDiscount.getId());
                            if (discount1.isPresent()){
                                if (!discount1.get().isValid())
                                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                            "Discount with 'id' has expired");
                            }
                        }
                        // Modifiers Validation
                        List<Modifier> modifiers = item.getModifiers();

                        if (modifiers!=null){
                            for (Modifier modifier: modifiers
                                 ) {
                                // Existence
                                if (!modifierRepository.existsById(modifier.getId()))
                                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "No such modifier with id '"+modifier.getId()+"'");
                                // External rules validation is done only with FULL type validation
                            }
                        }

                    }
                }
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.DESTINATION){
                Location destination = (Location) checkoutAttribute.getAttribute();
                if (destination==null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Origin location object is mandatory");
                Boolean validLocationFormat = destination.isValid(); // Not implemented yet
                if (validLocationFormat==false)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Destination location object has no valid format");
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.ORIGIN){
                Location origin = (Location) checkoutAttribute.getAttribute();
                if (origin==null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Origin location object is mandatory");
                Boolean validLocationFormat = origin.isValid(); // Not implemented yet
                if (validLocationFormat==false)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Origin location object has no valid format");
            }
            if (checkoutAttribute.getName()==CheckoutAttributeName.PAYMENT){
                // Future implementation
            }
        }
    }

    // SIMPLE VALIDATION
    // Here we validate if customer has provided a properly populated minimum checkout
    // Minimum is: Origin, Store Variant and Cart with one Line Item (Only product is considered)
    private void validateSenderCheckout(Checkout checkout){
        Location origin = checkout.getOrigin();
        if (origin==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Origin location object is mandatory");
        Boolean validLocationFormat = origin.isValid(); // Not implemented yet
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
