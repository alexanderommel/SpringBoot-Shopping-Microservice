package com.example.tongue.messaging;

import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.checkout.*;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.integration.orders.Order;
import com.example.tongue.integration.orders.OrderAccepted;
import com.example.tongue.integration.orders.OrderRepository;
import com.example.tongue.repositories.FulfillmentRepository;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderQueueConsumerTest {

    @Autowired
    CheckoutRepository checkoutRepository;
    @Autowired
    CustomerReplicationRepository customerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    FulfillmentRepository fulfillmentRepository;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ShippingQueuePublisher shippingQueuePublisher;

    OrderQueueConsumer orderQueueConsumer;
    Checkout checkout;
    Fulfillment fulfillment;

    @Before
    public void setUp(){
        Map<String, Object> map = createCheckoutAndFulfillment();
        this.fulfillment = (Fulfillment) map.get("FULFILLMENT");
        this.checkout = (Checkout) map.get("CHECKOUT");

        orderQueueConsumer = new OrderQueueConsumer(
                "span",
                orderRepository,
                fulfillmentRepository,
                shippingQueuePublisher,
                objectMapper
        );

    }

    @Test
    public void shouldPublishShippingRequestMessageWhenConsumingOrderAccepted(){
        OrderAccepted orderAccepted = OrderAccepted.builder()
                .checkoutId(checkout.getId())
                .orderId("12521")
                .build();

        orderQueueConsumer.consumeOrderAcceptedMessage(orderAccepted);
        Mockito.verify(shippingQueuePublisher, Mockito.times(1))
                .publishShippingRequest(ArgumentMatchers.any());
    }

    @Test
    public void shouldAddReceivedOrderToFulfillmentWhenConsumingOrderAccepted(){

        OrderAccepted orderAccepted = OrderAccepted.builder()
                .checkoutId(checkout.getId())
                .orderId("12521")
                .build();

        orderQueueConsumer.consumeOrderAcceptedMessage(orderAccepted);
        Order order = fulfillmentRepository.findByCheckoutId(checkout.getId()).get().getOrder();
        boolean expected = order.getOrderId()==orderAccepted.getOrderId();
        assertTrue(expected);

    }

    private Map<String, Object> createCheckoutAndFulfillment(){

        Map<String, Object> map = new HashMap<>();

        Customer customer = Customer.builder()
                .username("alexander")
                .build();

        customer = customerRepository.save(customer);

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .shippingSession("span")
                .fee(BigDecimal.ZERO)
                .customerPosition(Position.builder().build())
                .storePosition(Position.builder().build())
                .build();

        Checkout checkout = Checkout.builder()
                .customer(customer)
                .paymentInfo(PaymentInfo.builder().paymentMethod(PaymentInfo.PaymentMethod.CREDIT_CARD).build())
                .price(CheckoutPrice.builder().cartSubtotal(BigDecimal.TEN).cartTotal(BigDecimal.TEN).build())
                .shippingInfo(shippingInfo)
                .build();

        checkout = checkoutRepository.save(checkout);

        Fulfillment fulfillment = Fulfillment.builder()
                .checkout(checkout)
                .build();

        fulfillment = fulfillmentRepository.save(fulfillment);

        map.put("FULFILLMENT",fulfillment);
        map.put("CHECKOUT",checkout);

        return map;
    }

}