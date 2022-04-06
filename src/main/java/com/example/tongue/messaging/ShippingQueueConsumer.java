package com.example.tongue.messaging;

import com.example.tongue.domain.checkout.Fulfillment;
import com.example.tongue.integration.orders.OrderConfirmation;
import com.example.tongue.integration.payments.Payment;
import com.example.tongue.integration.payments.PaymentServiceBroker;
import com.example.tongue.integration.shipping.ShipmentAcceptation;
import com.example.tongue.integration.shipping.ShipmentContinuation;
import com.example.tongue.integration.shipping.Shipping;
import com.example.tongue.integration.shipping.ShippingRepository;
import com.example.tongue.repositories.checkout.FulfillmentRepository;
import com.example.tongue.services.FulfillmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ShippingQueueConsumer {

    private String acceptedQueueName;
    private String shippingContinuationQueueName;
    private PaymentServiceBroker paymentServiceBroker;
    private ShippingRepository shippingRepository;
    private FulfillmentRepository fulfillmentRepository;
    private OrderQueuePublisher orderQueuePublisher;
    private FulfillmentService fulfillmentService;

    public ShippingQueueConsumer(@Value("${shopping.queues.in.shipping.accept}") String acceptedQueueName,
                                 @Value("${shopping.queues.in.shipping.continue}") String shippingContinuationQueueName,
                                 @Autowired PaymentServiceBroker paymentServiceBroker,
                                 @Autowired ShippingRepository shippingRepository,
                                 @Autowired FulfillmentRepository fulfillmentRepository,
                                 @Autowired OrderQueuePublisher orderQueuePublisher,
                                 @Autowired FulfillmentService fulfillmentService){

        this.acceptedQueueName=acceptedQueueName;
        this.shippingContinuationQueueName=shippingContinuationQueueName;
        this.paymentServiceBroker=paymentServiceBroker;
        this.shippingRepository=shippingRepository;
        this.fulfillmentRepository=fulfillmentRepository;
        this.orderQueuePublisher=orderQueuePublisher;
        this.fulfillmentService=fulfillmentService;

    }

    @RabbitListener(queues = "${shopping.queues.in.shipping.continue}")
    public void receiveShipmentContinuationMessage(ShipmentContinuation shipmentContinuation){
        log.info("Consuming ShipmentAccepted Message from Queue: "+shippingContinuationQueueName);
        log.info("Processing Content: "+shipmentContinuation);
        fulfillmentService.shipOrder(shipmentContinuation);
    }

    @RabbitListener(queues = {"${shopping.queues.in.shipping.accept}"})
    public void receiveShipmentAcceptedMessage(ShipmentAcceptation message){
        log.info("Consuming ShipmentAccepted Message from Queue: "+acceptedQueueName);
        log.info("Processing Content: "+message);
        log.info("Calling Payment Service Broker to create a payment");

        /** Payments not implemented yet!!! **/
        Payment payment = paymentServiceBroker.createPayment();

        Optional<Fulfillment> wrapper =
                fulfillmentRepository.findByCheckoutId(Long.valueOf(message.getArtifactId()));
        if (wrapper.isEmpty()){
            log.error("There's no checkout instance assigned to the received ID!!!");
        }

        Fulfillment fulfillment = wrapper.get();

        Shipping shipping = Shipping.builder()
                .shippingId(message.getShippingId())
                .driverUsername(message.getDriverUsername())
                .build();

        fulfillment.setShipping(shipping);
        fulfillment.setPayment(payment);

        fulfillment = fulfillmentRepository.save(fulfillment);

        OrderConfirmation confirmation = OrderConfirmation.builder()
                .orderId(fulfillment.getOrder().getOrderId())
                .courierName(shipping.getDriverUsername())
                .customerName(fulfillment.getCheckout().getCustomer().getUsername())
                .build();

        orderQueuePublisher.publishOrderConfirmation(confirmation);

    }
}
