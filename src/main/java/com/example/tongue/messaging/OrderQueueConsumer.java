package com.example.tongue.messaging;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.Fulfillment;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.integration.orders.Order;
import com.example.tongue.integration.orders.OrderAccepted;
import com.example.tongue.integration.orders.OrderRepository;
import com.example.tongue.integration.shipping.Artifact;
import com.example.tongue.integration.shipping.Shipping;
import com.example.tongue.integration.shipping.ShippingRequest;
import com.example.tongue.repositories.FulfillmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Slf4j
public class OrderQueueConsumer {

    private String orderAcceptedQueue;
    private OrderRepository orderRepository;
    private FulfillmentRepository fulfillmentRepository;
    private ShippingQueuePublisher shippingQueuePublisher;
    private ObjectMapper mapper;

    public OrderQueueConsumer(
            @Value("${shopping.queues.in.order.accept}") String orderAcceptedQueue,
            @Autowired OrderRepository orderRepository,
            @Autowired FulfillmentRepository fulfillmentRepository,
            @Autowired ShippingQueuePublisher shippingQueuePublisher,
            @Autowired ObjectMapper mapper){

        this.orderAcceptedQueue=orderAcceptedQueue;
        this.orderRepository=orderRepository;
        this.fulfillmentRepository=fulfillmentRepository;
        this.shippingQueuePublisher=shippingQueuePublisher;
        this.mapper=mapper;
    }

    @RabbitListener(queues = {"${shopping.queues.in.order.accept}"})
    public void consumeOrderAcceptedMessage(OrderAccepted message){
        log.info("Consuming OrderAccepted from Queue: "+orderAcceptedQueue);
        log.info("Processing Content: "+message);
        log.info("Creating order");
        Order order = Order.builder().orderId(message.getOrderId()).build();
        order = orderRepository.save(order);
        Optional<Fulfillment> wrapper = fulfillmentRepository.findByCheckoutId(message.getCheckoutId());
        if (wrapper.isEmpty()){
            log.error("Checkouts must be attached with a fulfillment");
            return;
        }
        log.info("Attaching order to fulfillment");
        Fulfillment fulfillment = wrapper.get();
        fulfillment.setOrder(order);
        fulfillment = fulfillmentRepository.save(fulfillment);
        log.info("Creating Shipping Request");
        Checkout c = fulfillment.getCheckout();

        ShippingRequest.PaymentMethod paymentMethod= ShippingRequest.PaymentMethod.CASH;
        if (c.getPaymentInfo().getPaymentMethod()== PaymentInfo.PaymentMethod.CREDIT_CARD)
            paymentMethod = ShippingRequest.PaymentMethod.CREDIT;

        /** Current version doesn't support customer debts **/
        ShippingRequest.Billing billing = ShippingRequest.Billing.builder()
                .artifact(c.getPrice().getCartSubtotal())
                .fee(c.getShippingInfo().getFee())
                .paymentMethod(paymentMethod)
                .hasDebts(false)
                .debt(BigDecimal.ZERO)
                .build();

        Artifact artifact = Artifact.builder()
                .artifactId(String.valueOf(message.getCheckoutId()))
                .owner(c.getCustomer().getUsername())
                .build();

        ShippingRequest request = ShippingRequest.builder()
                .artifact(artifact)
                .billing(billing)
                .origin(c.getShippingInfo().getCustomerPosition())
                .destination(c.getShippingInfo().getStorePosition())
                .shippingFeeToken(c.getShippingInfo().getShippingSession())
                .testing(true) // ignores fee token validation
                .build();

        shippingQueuePublisher.publishShippingRequest(request);

    }

}
