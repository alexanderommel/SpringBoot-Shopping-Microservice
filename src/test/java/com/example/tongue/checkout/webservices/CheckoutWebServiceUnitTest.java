package com.example.tongue.checkout.webservices;

import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.services.CheckoutCompletionFlow;
import com.example.tongue.services.CheckoutCreationFlow;
import com.example.tongue.services.CheckoutUpgradeFlow;
import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.FlowMessage;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import com.example.tongue.core.converters.CheckoutAttributeConverter;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.resources.checkout.CheckoutWebService;
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
    private CheckoutAttributeConverter attributeConverter;
    @Mock
    private HttpSession httpSession;

    private CheckoutWebService checkoutWebService;

    @Before
    public void setUp(){
        checkoutWebService = new CheckoutWebService(
                checkoutRepository,
                completionFlow,
                creationFlow,
                upgradeFlow,
                attributeConverter
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
    public void shouldReturnHttpStatusBAD_REQUESTWhenCreatingIfCheckoutIsValid(){
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
    public void shouldReturnSameProductIdInRequestBodyIfCheckoutIsValidWhenCreating(){
        Long expected = 1L;
        Checkout checkout = new Checkout();
        Product product = new Product(); product.setId(1L);
        LineItem item =  new LineItem(); ShoppingCart shoppingCart = new ShoppingCart();
        item.setProduct(product); shoppingCart.addItem(item);
        checkout.setShoppingCart(shoppingCart);
        FlowMessage flowMessage = new FlowMessage();
        flowMessage.setSolved(true);
        flowMessage.setAttribute(checkout,"checkout");
        Mockito.when(creationFlow.run(checkout,httpSession)).thenReturn(flowMessage);
        ResponseEntity<Map<String,Object>> response =
                checkoutWebService.create(httpSession,checkout);
        Long current = ((Checkout) response.getBody().get("response"))
                .getShoppingCart().getItems().get(0).getProduct().getId();
        assertEquals(expected,current);
    }



}