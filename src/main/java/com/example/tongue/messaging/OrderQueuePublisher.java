package com.example.tongue.messaging;

import com.example.tongue.integration.orders.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderQueuePublisher {

    private RabbitTemplate rabbitTemplate;
    private String orderRequestQueue;

    public OrderQueuePublisher(@Autowired RabbitTemplate rabbitTemplate,
                               @Value("${shopping.queues.out.order.request}") String orderRequestQueueName) {
        this.rabbitTemplate=rabbitTemplate;
        this.orderRequestQueue=orderRequestQueueName;
    }

    @Async
    public void publishOrderRequest(OrderRequest orderRequest){
        log.info("Publishing Order Request to Queue: "+orderRequestQueue);
        log.info("Order request -> "+orderRequest);
        rabbitTemplate.convertAndSend(orderRequestQueue, orderRequest);
    }

}
