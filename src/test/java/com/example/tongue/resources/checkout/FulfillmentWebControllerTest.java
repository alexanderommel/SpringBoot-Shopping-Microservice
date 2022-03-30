package com.example.tongue.resources.checkout;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.CheckoutPrice;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.messaging.OrderQueuePublisher;
import com.example.tongue.repositories.checkout.FulfillmentRepository;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FulfillmentWebControllerTest {

    @Autowired
    private FulfillmentRepository fulfillmentRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private CustomerReplicationRepository customerReplicationRepository;

    @MockBean
    private OrderQueuePublisher orderQueuePublisher;

    Customer customer;
    Checkout checkout;

    @Before
    public void setUp(){
        Map<String, Object> map = createCheckoutAndCustomer();
        this.customer = (Customer) map.get("CUSTOMER");
        this.checkout = (Checkout) map.get("CHECKOUT");
    }

    @Test
    public void givenThatCheckoutDoesntExistsThenHttpStatusIsNotOK(){
        FulfillmentWebController controller = new FulfillmentWebController(
                fulfillmentRepository,
                checkoutRepository,
                orderQueuePublisher
        );

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("alexander");
        checkout.setId(5L);
        ResponseEntity responseEntity = controller.begin(checkout.getId(),principal);
        boolean condition = !responseEntity.getStatusCode().is2xxSuccessful();
        assertTrue(condition);
    }

    @Test
    public void givenThatPrincipalHasNoAccessThenHttpStatusIsNotOK(){
        FulfillmentWebController controller = new FulfillmentWebController(
                fulfillmentRepository,
                checkoutRepository,
                orderQueuePublisher
        );

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("dummy");
        ResponseEntity responseEntity = controller.begin(checkout.getId(),principal);
        boolean condition = !responseEntity.getStatusCode().is2xxSuccessful();
        assertTrue(condition);
    }

    @Test
    public void givenThatCheckoutAndPrincipalAreOkWhenBeginningThenOrderRequestMustBePublished(){
        FulfillmentWebController controller = new FulfillmentWebController(
                fulfillmentRepository,
                checkoutRepository,
                orderQueuePublisher
        );
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("alexander");
        controller.begin(checkout.getId(),principal);
        Mockito.verify(orderQueuePublisher, Mockito.times(1))
                .publishOrderRequest(ArgumentMatchers.any());
    }

    private Map<String, Object> createCheckoutAndCustomer(){

        Map<String,Object> map = new HashMap<>();

        Customer customer = Customer.builder()
                .username("alexander")
                .build();

        customer = customerReplicationRepository.save(customer);

        Checkout checkout = Checkout.builder()
                .customer(customer)
                .paymentInfo(PaymentInfo.builder().paymentMethod(PaymentInfo.PaymentMethod.CREDIT_CARD).build())
                .price(CheckoutPrice.builder().cartSubtotal(BigDecimal.TEN).cartTotal(BigDecimal.TEN).build())
                .build();

        checkout = checkoutRepository.save(checkout);

        map.put("CUSTOMER",customer);
        map.put("CHECKOUT",checkout);

        return map;

    }

}