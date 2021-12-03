package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.integrations.shipping.ShippingBrokerResponse;
import com.example.tongue.integrations.shipping.ShippingSummary;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Modifier;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import com.example.tongue.shopping.models.Order;
import com.example.tongue.shopping.repositories.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

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
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private CheckoutCompletionFlow completionFlow;

    @Before
    public void setUp(){
        /** In Session Checkout Mocking**/
        Checkout checkout = new Checkout();
        Location origin = new Location();
        origin.setLatitude(1333.0F);
        origin.setLongitude(552.2F);
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
    public void givenCheckoutWhenCreatingOrderThenOrderCartIsEqualToCheckoutCart(){
        Checkout checkout = checkoutRepository.save(null);
        BigDecimal expected = checkout.getCart().getItems().get(0).getProduct().getPrice();
        Order order =
                completionFlow.createOrderFromCheckout(checkout);
        BigDecimal current =
                order.getCart().getItems().get(0).getProduct().getPrice();
        assertTrue(expected.equals(current));
    }

    @Test
    public void givenCheckoutWhenRunningThenOrderCartProductPriceIsEqualToCheckoutCartProductPrice(){
        /** Mocking **/
        Checkout checkout = checkoutRepository.save(null);
        BigDecimal expected = checkout.getCart().getItems().get(0).getProduct().getPrice();
        Order order =
                completionFlow.createOrderFromCheckout(checkout);
        order.setId(10L);
        Mockito.when(orderRepository.save(ArgumentMatchers.any())).thenReturn(order);
        /** Running **/
        FlowMessage flowMessage = completionFlow.run(httpSession);
        Order retrieved = (Order) flowMessage.getAttribute("order");
        BigDecimal current =
                retrieved.getCart().getItems().get(0).getProduct().getPrice();
        assertTrue(expected.equals(current));
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