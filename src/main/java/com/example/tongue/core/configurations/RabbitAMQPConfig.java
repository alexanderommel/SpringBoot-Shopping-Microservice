package com.example.tongue.core.configurations;

import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@Slf4j
public class RabbitAMQPConfig {

    private String orderRequestQueueName;
    private String orderAcceptedQueueName;
    private String shippingRequestQueueName;
    private String host;
    private String user;
    private String password;
    private String virtualHost;
    private Integer port;

    public RabbitAMQPConfig(@Value("${shopping.queues.out.order.request}") String orderRequestQueueName,
                            @Value("${shopping.queues.in.order.accept}") String orderAcceptedQueueName,
                            @Value("${shopping.queues.out.shipping.request}") String shippingRequestQueueName,
                            @Value("${spring.rabbitmq.host}") String host,
                            @Value("${spring.rabbitmq.port}") Integer port,
                            @Value("${spring.rabbitmq.username}") String user,
                            @Value("${spring.rabbitmq.password}") String password){

        this.orderRequestQueueName=orderRequestQueueName;
        this.orderAcceptedQueueName=orderAcceptedQueueName;
        this.shippingRequestQueueName=shippingRequestQueueName;
        this.host=host;
        this.port=port;
        this.user=user;
        this.password=password;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws Exception{
        log.info("Setting Connection Factory");
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws  Exception{
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Queue orderRequestQueue(){
        return new Queue(orderRequestQueueName, false, false ,false);
    }

    @Bean
    public Queue orderAcceptedQueue(){
        return new Queue(orderAcceptedQueueName, false, false, false);
    }

    @Bean
    public Queue shippingRequestQueue(){
        return new Queue(shippingRequestQueueName, false, false, false);
    }
}
