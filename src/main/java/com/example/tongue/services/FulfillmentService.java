package com.example.tongue.services;

import com.example.tongue.domain.checkout.Fulfillment;
import com.example.tongue.integration.orders.OrderCompleted;
import com.example.tongue.integration.payments.Payment;
import com.example.tongue.integration.payments.PaymentRepository;
import com.example.tongue.integration.payments.PaymentServiceBroker;
import com.example.tongue.integration.shipping.ShipmentContinuation;
import com.example.tongue.messaging.OrderQueuePublisher;
import com.example.tongue.repositories.checkout.FulfillmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FulfillmentService {

    private FulfillmentRepository fulfillmentRepository;
    private PaymentServiceBroker paymentServiceBroker;
    private OrderQueuePublisher orderQueuePublisher;
    private PaymentRepository paymentRepository;

    public FulfillmentService(@Autowired FulfillmentRepository fulfillmentRepository,
                              @Autowired PaymentServiceBroker paymentServiceBroker,
                              @Autowired OrderQueuePublisher orderQueuePublisher,
                              @Autowired PaymentRepository paymentRepository){
        this.fulfillmentRepository=fulfillmentRepository;
        this.paymentServiceBroker=paymentServiceBroker;
        this.orderQueuePublisher=orderQueuePublisher;
        this.paymentRepository=paymentRepository;
    }

    public void shipOrder(ShipmentContinuation s){
        log.info("Order ");
        Fulfillment fulfillment =
                fulfillmentRepository.findByCheckoutId(Long.valueOf(s.getArtifactId())).get();
        Payment payment = paymentServiceBroker.continueShoppingPayment(fulfillment.getPayment());
        payment = paymentRepository.save(payment);
        fulfillment.setPayment(payment);
        fulfillment = fulfillmentRepository.save(fulfillment);
        OrderCompleted orderCompleted = OrderCompleted.builder()
                .orderId(fulfillment.getOrder().getOrderId())
                .build();
        orderQueuePublisher.publishOrderCompletion(orderCompleted);
    }

}
