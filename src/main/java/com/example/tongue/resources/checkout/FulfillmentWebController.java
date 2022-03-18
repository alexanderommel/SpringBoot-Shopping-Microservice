package com.example.tongue.resources.checkout;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.Fulfillment;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.integration.orders.Order;
import com.example.tongue.integration.orders.OrderRequest;
import com.example.tongue.messaging.OrderQueuePublisher;
import com.example.tongue.repositories.FulfillmentRepository;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class FulfillmentWebController {

    private FulfillmentRepository fulfillmentRepository;
    private CheckoutRepository checkoutRepository;
    private OrderQueuePublisher orderQueuePublisher;

    public FulfillmentWebController(@Autowired FulfillmentRepository fulfillmentRepository,
                                    @Autowired CheckoutRepository checkoutRepository,
                                    @Autowired OrderQueuePublisher orderQueuePublisher){

        this.fulfillmentRepository=fulfillmentRepository;
        this.checkoutRepository=checkoutRepository;
        this.orderQueuePublisher=orderQueuePublisher;
    }

    @PostMapping("/fulfillment/begin")
    public ResponseEntity<Map<String,Object>> begin(@RequestParam Long checkout_id, Principal principal){
        log.info("Begin Fulfillment");
        Map<String, Object> response = new HashMap<>();
        Optional<Checkout> wrapper = checkoutRepository.findById(checkout_id);
        if (wrapper.isEmpty()){
            log.warn("No such Checkout with id->"+checkout_id);
            response.put("error","No such Checkout with id->"+checkout_id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Checkout checkout = wrapper.get();
        if (checkout.getCustomer().getUsername().equalsIgnoreCase(principal.getName())){
            log.warn("A user who's not the owner is trying to access a Checkout instance");
            response.put("error","Forbidden Access for User");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        log.info("Creating Order Request and Fulfillment");
        OrderRequest.Billing billing = new OrderRequest.Billing();
        if (checkout.getPaymentInfo().getPaymentMethod()== PaymentInfo.PaymentMethod.CASH){
            billing = OrderRequest.Billing.builder()
                    .paymentMethod(OrderRequest.PaymentMethod.CASH)
                    .total(checkout.getPrice().getCartSubtotal())
                    .build();
        }
        if (checkout.getPaymentInfo().getPaymentMethod()== PaymentInfo.PaymentMethod.CREDIT_CARD){
            billing = OrderRequest.Billing.builder()
                    .paymentMethod(OrderRequest.PaymentMethod.CREDIT)
                    .total(checkout.getPrice().getCartSubtotal())
                    .build();
        }
        OrderRequest orderRequest = OrderRequest.builder()
                .artifactId(String.valueOf(checkout.getId()))
                .shoppingCart(checkout.getShoppingCart())
                .billing(billing)
                .build();
        Fulfillment fulfillment = Fulfillment.builder()
                .checkout(checkout)
                .build();
        fulfillment = fulfillmentRepository.save(fulfillment);
        orderQueuePublisher.publishOrderRequest(orderRequest);
        response.put("response",fulfillment);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
