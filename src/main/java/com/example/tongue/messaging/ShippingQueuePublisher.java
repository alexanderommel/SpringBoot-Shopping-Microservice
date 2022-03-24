package com.example.tongue.messaging;

import com.example.tongue.integration.shipping.ShippingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShippingQueuePublisher {

    private RabbitTemplate rabbitTemplate;
    private String shippingRequestQueue;

    public ShippingQueuePublisher(
            @Autowired RabbitTemplate rabbitTemplate,
            @Value("${shopping.queues.out.shipping.request}") String shippingRequestQueue){
        this.rabbitTemplate=rabbitTemplate;
        this.shippingRequestQueue=shippingRequestQueue;
    }

    @Async
    public void publishShippingRequest(ShippingRequest shippingRequest){
        log.info("Publishing Shipping Request message to queue->"+shippingRequestQueue);
        log.info("Shipping Request: "+shippingRequest);
        rabbitTemplate.convertAndSend(shippingRequestQueue, shippingRequest);
    }
}
