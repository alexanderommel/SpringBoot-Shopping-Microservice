package com.example.tongue.checkout.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.domain.checkout.ValidationResponse;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.Cart;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.services.CheckoutCompletionFlow;
import com.example.tongue.services.CheckoutSession;
import com.example.tongue.services.CheckoutValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutCompletionFlowUnitTest {

    @Mock
    private CheckoutValidation checkoutValidation;
    @Mock
    private CheckoutSession checkoutSession;
    @Mock
    private HttpSession httpSession;
    @Mock
    private CheckoutRepository checkoutRepository;
    @InjectMocks
    private CheckoutCompletionFlow completionFlow;

    @Before
    public void setUp(){
        /** In Session Checkout Mocking**/
        Checkout checkout = new Checkout();
        Position origin = Position.builder().latitude(1333F).longitude(552.2F).build();
        checkout.setOrigin(null);
        checkout.setDestination(null);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setStoreVariant(storeVariant);
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(2L);
        product.setPrice(BigDecimal.valueOf(6.65));
        item1.setQuantity(2);
        item1.setProduct(product);
        item1.setInstructions("Test instruction");
        cart.addItem(item1);
        checkout.setCart(cart);
        Mockito.when(checkoutSession.get(ArgumentMatchers.any())).thenReturn(checkout);
        Mockito.when(checkoutSession.save(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(checkout);
        Mockito.when(checkoutSession.delete(ArgumentMatchers.any())).thenReturn(null);
        /** CheckoutValidation Mocking**/
        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setSolved(true);
        Mockito.when(checkoutValidation.hardValidation(ArgumentMatchers.any())).thenReturn(validationResponse);
        /** Checkout Repository Mocking**/
        checkout.setId(5L);
        Mockito.when(checkoutRepository.save(ArgumentMatchers.any())).thenReturn(checkout);
    }

    @Test
    public void givenNoExistingCheckoutOnSessionWhenRunningThenReturnFalse(){
        Mockito.when(checkoutSession.get(httpSession)).thenReturn(null);
        FlowMessage flowMessage = completionFlow.run(httpSession);
        assertFalse(flowMessage.isSolved());
    }

    @Test
    public void shouldReturnNotNullFinishedAtFieldRunning(){
        FlowMessage flowMessage = completionFlow.run(httpSession);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        Instant instant = checkout.getFinishedAt();
        assertNotNull(instant);
    }

}