package com.example.tongue.resources.checkout;

import com.example.tongue.core.contracts.ApiResponse;
import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.Fulfillment;
import com.example.tongue.domain.checkout.PaymentInfo;
import com.example.tongue.integration.orders.OrderRequest;
import com.example.tongue.integration.shipping.Artifact;
import com.example.tongue.integration.shipping.ShippingRequest;
import com.example.tongue.messaging.OrderQueuePublisher;
import com.example.tongue.messaging.ShippingQueuePublisher;
import com.example.tongue.repositories.checkout.FulfillmentRepository;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

    private ShippingQueuePublisher shippingQueuePublisher;

    public FulfillmentWebController(@Autowired FulfillmentRepository fulfillmentRepository,
                                    @Autowired CheckoutRepository checkoutRepository,
                                    @Autowired OrderQueuePublisher orderQueuePublisher,
                                    @Autowired ShippingQueuePublisher shippingQueuePublisher){

        this.fulfillmentRepository=fulfillmentRepository;
        this.checkoutRepository=checkoutRepository;
        this.orderQueuePublisher=orderQueuePublisher;
        this.shippingQueuePublisher = shippingQueuePublisher;
    }

    @PostMapping("/fulfillment/begin")
    public ResponseEntity<ApiResponse> begin(@RequestBody Checkout checkout11, Principal principal){
        log.info("Begin Fulfillment");
        Long id = checkout11.getId();
        Optional<Checkout> wrapper = checkoutRepository.findById(id);
        if (wrapper.isEmpty()){
            log.warn("No such Checkout with id->"+id);
            return new ResponseEntity<>(ApiResponse.error("No such Checkout with that id"),
                    HttpStatus.NOT_FOUND);
        }
        Checkout checkout = wrapper.get();
        if (!checkout.getCustomer().getUsername().equalsIgnoreCase(principal.getName())){
            log.warn("A user who's not the owner is trying to access a Checkout instance");
            return new ResponseEntity<>(ApiResponse.error("Forbidden Access for User"),
                    HttpStatus.FORBIDDEN);
        }
        log.info("Creating Order Request and Fulfillment");
        PaymentInfo.PaymentMethod paymentMethod = null;
        if (checkout.getPaymentInfo().getPaymentMethod()== PaymentInfo.PaymentMethod.CASH){
            paymentMethod = PaymentInfo.PaymentMethod.CASH;
        }
        else{
            paymentMethod = PaymentInfo.PaymentMethod.CREDIT_CARD;
        }
        OrderRequest orderRequest = OrderRequest.builder()
                .artifactId(String.valueOf(checkout.getId()))
                .shoppingCartId(String.valueOf(checkout.getShoppingCart().getId()))
                .total(checkout.getPrice().getCartSubtotal())
                .paymentMethod(paymentMethod)
                .build();

        Fulfillment fulfillment = Fulfillment.builder()
                .checkout(checkout)
                .build();
        orderQueuePublisher.publishOrderRequest(orderRequest);
        fulfillment = fulfillmentRepository.save(fulfillment);

        /** TEMPORAL **/
        publishShippingRequestTemporalMethod(fulfillment.getCheckout());
        /** **/

        log.info("Call solved properly");
        return ResponseEntity.ok(ApiResponse.success(fulfillment));
    }

    private void publishShippingRequestTemporalMethod(Checkout c){

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
                .artifactId(String.valueOf(c.getId()))
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
        log.info("Ok");

    }

}
