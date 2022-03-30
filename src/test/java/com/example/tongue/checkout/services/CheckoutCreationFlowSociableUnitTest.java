package com.example.tongue.checkout.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.checkout.ShippingInfo;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.services.CheckoutCreationFlow;
import com.example.tongue.services.CheckoutSession;
import com.example.tongue.services.CheckoutValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutCreationFlowSociableUnitTest {

    @Mock
    private HttpSession httpSession;

    @Mock
    CheckoutSession checkoutSession;

    @Mock
    private StoreVariantRepository storeVariantRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModifierRepository modifierRepository;

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private CheckoutValidation checkoutValidation;

    @BeforeAll
    public void setUp(){
        checkoutValidation = new CheckoutValidation(discountRepository,
                storeVariantRepository,
                productRepository,
                modifierRepository);
    }

    @Before
    public void mockWhen(){
        Mockito.when(storeVariantRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.existsById(3L)).thenReturn(true);
    }

    @Test
    public void givenEmptyCheckoutWhenRunningThenReturnFalse(){
        Checkout checkout = new Checkout();
        CheckoutCreationFlow creationFlow = new CheckoutCreationFlow(
                checkoutValidation,
                checkoutSession
        );
        FlowMessage message = creationFlow.run(checkout,httpSession);
        assertFalse(message.isSolved());
    }

    @Test
    public void givenEmptyCheckoutWhenRunningThenReturnErrorStage(){
        String expected = "Validation error";
        Checkout checkout = new Checkout();
        CheckoutCreationFlow creationFlow = new CheckoutCreationFlow(
                checkoutValidation,
                checkoutSession
        );
        FlowMessage message = creationFlow.run(checkout,httpSession);
        assertEquals(expected,message.getErrorStage());
    }

    @Test
    public void givenCheckoutWithNoExistingProductWhenRunningThenReturnErrorStage(){
        String expected = "Validation error";
        Checkout checkout = new Checkout();
        Position origin = Position.builder().longitude(11.f).latitude(11.2f).build();
        StoreVariant storeVariant = new StoreVariant(); storeVariant.setId(1L);
        Product product = new Product(); product.setId(2L);
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item = new LineItem();
        item.setProduct(product);
        shoppingCart.addItem(item);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(null).build();
        checkout.setShippingInfo(shippingInfo);
        checkout.setStoreVariant(storeVariant);
        checkout.setShoppingCart(shoppingCart);
        CheckoutCreationFlow creationFlow = new CheckoutCreationFlow(
                checkoutValidation,
                checkoutSession
        );
        FlowMessage message = creationFlow.run(checkout,httpSession);
        assertEquals(expected,message.getErrorStage());
    }

    @Test
    public void givenValidCheckoutWhenRunningThenReturnTrue(){
        Checkout checkout = new Checkout();
        Position origin = Position.builder().longitude(11.f).latitude(11.2f).build();
        StoreVariant storeVariant = new StoreVariant(); storeVariant.setId(1L);
        Product product = new Product(); product.setId(3L);
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item = new LineItem();
        item.setProduct(product);
        shoppingCart.addItem(item);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(null).build();
        checkout.setShippingInfo(shippingInfo);
        checkout.setStoreVariant(storeVariant);
        checkout.setShoppingCart(shoppingCart);
        CheckoutCreationFlow creationFlow = new CheckoutCreationFlow(
                checkoutValidation,
                checkoutSession
        );
        FlowMessage message = creationFlow.run(checkout,httpSession);
        assertTrue(message.isSolved());
    }

}