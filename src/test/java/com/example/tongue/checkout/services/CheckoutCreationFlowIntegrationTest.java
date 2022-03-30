package com.example.tongue.checkout.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.checkout.ShippingInfo;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.repositories.merchant.*;
import com.example.tongue.services.CheckoutCreationFlow;
import com.example.tongue.services.CheckoutSession;
import com.example.tongue.services.CheckoutValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CheckoutCreationFlowIntegrationTest {

    private CheckoutCreationFlow checkoutCreationFlow;
    //
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    StoreVariantRepository storeVariantRepository;
    @Autowired
    DiscountRepository discountRepository;
    @Autowired
    ProductImageRepository imageRepository;
    @Autowired
    GroupModifierRepository groupModifierRepository;
    @Autowired
    ModifierRepository modifierRepository;
    @Autowired
    CollectionRepository collectionRepository;
    @Autowired
    HttpSession httpSession;
    @Autowired
    CheckoutValidation checkoutValidation;
    @Autowired
    CheckoutSession checkoutSession;

    //

    @BeforeAll
    public void setUp(){
        /** Generate data for testing **/
    }

    private void describeGeneratedData(){
    }

    @Before
    public void before(){
        checkoutCreationFlow = new CheckoutCreationFlow(checkoutValidation,checkoutSession);
        System.out.println("HTTP SESSION ID: "+httpSession.getId());
    }

    @Test
    public void showDataInformation(){
        System.out.println("---Data generated info---");
        describeGeneratedData();
        System.out.println("\n");
    }

    @Test
    public void shouldReturnTrueGivenThatCheckoutEntryIsValid(){
        /** Input **/
        Checkout checkout = new Checkout();
        checkout = createBasicValidCheckout();
        /** **/
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        System.out.println(flowMessage.getErrorMessage());
        assertTrue(flowMessage.isSolved());
    }

    @Test
    public void givenCheckoutWithoutOriginWhenCreatingThenReturnValidationErrorStage(){
        String expected = "Validation error";
        Checkout checkout = createBasicValidCheckout();
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(null).storePosition(null).build();
        checkout.setShippingInfo(shippingInfo);
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        assertEquals(flowMessage.getErrorStage(),expected);

    }

    @Test
    public void givenCheckoutWithNoExistingProductIdWhenCreatingThenReturnErrorMessage(){
        String expected = "No such Product with id '9994'";
        Checkout checkout = createBasicValidCheckout();
        checkout.getShoppingCart().getItems().get(0).getProduct().setId(9994L);
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        assertEquals(expected,flowMessage.getErrorMessage());
    }

    private Checkout createBasicValidCheckout(){
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setInstructions("McLoving");
        LineItem item= new LineItem();
        item.setQuantity(2);
        Product product = new Product();
        product.setId(371L);
        Position destination = Position.builder().latitude(22.2F).longitude(44.0F).build();
        Position origin = Position.builder().latitude(10.0F).longitude(5.552222F).build();
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(3L);
        item.setProduct(product);
        shoppingCart.addItem(item);
        checkout.setShoppingCart(shoppingCart);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        checkout.setStoreVariant(storeVariant);
        return checkout;
    }

}