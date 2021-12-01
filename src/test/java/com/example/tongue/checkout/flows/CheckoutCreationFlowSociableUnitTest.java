package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
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

    @Mock CheckoutSession checkoutSession;

    @Mock
    private StoreVariantRepository storeVariantRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CheckoutValidation checkoutValidation;

    @BeforeAll
    public void setUp(){
        checkoutValidation = new CheckoutValidation();
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
        Location origin = new Location(); origin.setLongitude(11.1f); origin.setLatitude(11.1f);
        StoreVariant storeVariant = new StoreVariant(); storeVariant.setId(1L);
        Product product = new Product(); product.setId(2L);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        item.setProduct(product);
        cart.addItem(item);
        checkout.setOrigin(origin);
        checkout.setStoreVariant(storeVariant);
        checkout.setCart(cart);
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
        Location origin = new Location(); origin.setLongitude(11.1f); origin.setLatitude(11.1f);
        StoreVariant storeVariant = new StoreVariant(); storeVariant.setId(1L);
        Product product = new Product(); product.setId(3L);
        Cart cart = new Cart();
        LineItem item = new LineItem();
        item.setProduct(product);
        cart.addItem(item);
        checkout.setOrigin(origin);
        checkout.setStoreVariant(storeVariant);
        checkout.setCart(cart);
        CheckoutCreationFlow creationFlow = new CheckoutCreationFlow(
                checkoutValidation,
                checkoutSession
        );
        FlowMessage message = creationFlow.run(checkout,httpSession);
        assertTrue(message.isSolved());
    }

}