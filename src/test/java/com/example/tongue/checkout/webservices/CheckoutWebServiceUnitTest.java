package com.example.tongue.checkout.webservices;

import com.example.tongue.checkout.flows.CheckoutCompletionFlow;
import com.example.tongue.checkout.flows.CheckoutCreationFlow;
import com.example.tongue.checkout.flows.CheckoutUpgradeFlow;
import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutWebServiceUnitTest {

    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private CheckoutCreationFlow creationFlow;
    @Mock
    private CheckoutUpgradeFlow upgradeFlow;
    @Mock
    private CheckoutCompletionFlow completionFlow;
    @Mock
    private HttpSession httpSession;

    private CheckoutWebService checkoutWebService;

    @Before
    public void setUp(){
        checkoutWebService = new CheckoutWebService(
                checkoutRepository,
                completionFlow,
                creationFlow,
                upgradeFlow
                );

    }

    @Test
    public void shouldReturnHttpStatusOKIfCheckoutIsValid(){
        Checkout checkout = new Checkout();
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setSolved(true);
        flowMessage.setAttribute(checkout,"checkout");
        Mockito.when(creationFlow.run(checkout,httpSession)).thenReturn(flowMessage);
        ResponseEntity<Map<String,Object>> response =
                checkoutWebService.create(httpSession,checkout);
        HttpStatus statusCode = response.getStatusCode();
        assertEquals(HttpStatus.OK,statusCode);
    }

    @Test
    public void shouldReturnHttpStatusBAD_REQUESTIfCheckoutIsValid(){
        Checkout checkout = new Checkout();
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setSolved(false);
        flowMessage.setAttribute(checkout,"checkout");
        Mockito.when(creationFlow.run(checkout,httpSession)).thenReturn(flowMessage);
        ResponseEntity<Map<String,Object>> response =
                checkoutWebService.create(httpSession,checkout);
        HttpStatus statusCode = response.getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST,statusCode);
    }

    @Test
    public void shouldReturnSameProductIdInRequestBodyIfCheckoutIsValid(){
        Long expected = 1L;
        Checkout checkout = new Checkout();
        Product product = new Product(); product.setId(1L);
        LineItem item =  new LineItem(); Cart cart = new Cart();
        item.setProduct(product); cart.addItem(item);
        checkout.setCart(cart);
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setSolved(true);
        flowMessage.setAttribute(checkout,"checkout");
        Mockito.when(creationFlow.run(checkout,httpSession)).thenReturn(flowMessage);
        ResponseEntity<Map<String,Object>> response =
                checkoutWebService.create(httpSession,checkout);
        Long current = ((Checkout) response.getBody().get("response"))
                .getCart().getItems().get(0).getProduct().getId();
        assertEquals(expected,current);
    }

}