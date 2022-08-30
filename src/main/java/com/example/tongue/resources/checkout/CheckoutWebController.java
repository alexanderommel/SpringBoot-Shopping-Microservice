package com.example.tongue.resources.checkout;

import com.example.tongue.core.contracts.ApiResponse;
import com.example.tongue.core.utilities.DataGenerator;
import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.integration.payments.PaymentServiceBroker;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.integration.shipping.ShippingServiceBroker;
import com.example.tongue.integration.shipping.ShippingSummary;
import com.example.tongue.repositories.checkout.CheckoutRepository;
import com.example.tongue.services.CheckoutCompletionFlow;
import com.example.tongue.services.CheckoutCreationFlow;
import com.example.tongue.services.CheckoutUpgradeFlow;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

@RestController
@Slf4j
public class CheckoutWebController {

    private CheckoutRepository checkoutRepository;
    private CheckoutCompletionFlow completionFlow;
    private CheckoutCreationFlow creationFlow;
    private CheckoutUpgradeFlow upgradeFlow;
    private CustomerReplicationRepository customerReplicationRepository;
    private PaymentServiceBroker paymentServiceBroker;
    private ShippingServiceBroker shippingServiceBroker;
    private Environment environment;
    private DataGenerator dataGenerator;

    public CheckoutWebController(@Autowired CheckoutRepository checkoutRepository,
                                 @Autowired CheckoutCompletionFlow completionFlow,
                                 @Autowired CheckoutCreationFlow creationFlow,
                                 @Autowired CheckoutUpgradeFlow upgradeFlow,
                                 @Autowired CustomerReplicationRepository customerReplicationRepository,
                                 @Autowired PaymentServiceBroker paymentServiceBroker,
                                 @Autowired ShippingServiceBroker shippingServiceBroker,
                                 @Autowired Environment environment,
                                 @Autowired DataGenerator dataGenerator){

        this.checkoutRepository = checkoutRepository;
        this.completionFlow=completionFlow;
        this.upgradeFlow=upgradeFlow;
        this.creationFlow=creationFlow;
        this.customerReplicationRepository=customerReplicationRepository;
        this.paymentServiceBroker = paymentServiceBroker;
        this.shippingServiceBroker=shippingServiceBroker;
        this.environment = environment;
        this.dataGenerator=dataGenerator;
    }


    @GetMapping(value = "/checkouts",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Checkout> checkoutPage = checkoutRepository.findAll(pageable);
        return getResponseEntityByPageable(checkoutPage);
    }

    /** TEMPORAL!!! **/

    @PostMapping(value = "/checkout/v2/complete", consumes = "application/json")
    public ResponseEntity<ApiResponse> complete(
            HttpSession session,
            @RequestBody Checkout checkout,
            Principal principal){

        log.info(checkout.toString());

        /** CREATE **/

        byte[] serializedCheckout = SerializationUtils.serialize(checkout);
        byte[] serializedCheckout2 = SerializationUtils.serialize(checkout);

        Checkout deserializedCopy = (Checkout) SerializationUtils.deserialize(serializedCheckout);
        Checkout deserializedCopy2 = (Checkout) SerializationUtils.deserialize(serializedCheckout2);

        log.info("Creating Checkout for session id->"+session.getId());
        FlowMessage message = creationFlow.run(deserializedCopy,session);
        if (!message.isSolved()){
            log.info("FlowMessage for Updating has errors ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }



        Checkout createdCheckout = (Checkout) message.getAttribute("checkout");

        /** UPDATE **/

        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();

        Checkout copy1 = deserializedCopy2;

        checkoutAttribute.setName(CheckoutAttributeName.CART);
        checkoutAttribute.setAttribute(copy1.getShoppingCart());

        FlowMessage updateCartMessage = upgradeFlow.run(checkoutAttribute,session);

        if (!updateCartMessage.isSolved()){
            log.info("FlowMessage for Attribute Updating has errors " +
                    "("+updateCartMessage.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(updateCartMessage.getErrorMessage()),
                    HttpStatus.BAD_REQUEST);
        }


        log.info(deserializedCopy2.toString());

        checkoutAttribute.setName(CheckoutAttributeName.PAYMENT);
        checkoutAttribute.setAttribute(deserializedCopy2.getPaymentInfo());

        FlowMessage updatePaymentMessage = upgradeFlow.run(checkoutAttribute,session);
        if (!updatePaymentMessage.isSolved()){
            log.info("FlowMessage for Attribute Updating has errors " +
                    "("+updatePaymentMessage.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(updatePaymentMessage.getErrorMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        Checkout copy3 = checkout;

        checkoutAttribute.setName(CheckoutAttributeName.SHIPPING);
        checkoutAttribute.setAttribute(copy3.getShippingInfo());

        FlowMessage updateShippingMessage = upgradeFlow.run(checkoutAttribute,session);
        if (!updateShippingMessage.isSolved()){
            log.info("FlowMessage for Attribute Updating has errors " +
                    "("+updateShippingMessage.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(updateShippingMessage.getErrorMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        /** COMPLETE **/

        log.info("Completing checkout for session id ->"+session.getId());

        Optional<Customer> optional = customerReplicationRepository.findByUsername(principal.getName());
        Object checkoutO = session.getAttribute("CHECKOUT");
        if (checkoutO==null){
            log.warn("Checkout not found on session!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Checkout almostCompletedCheckout = (Checkout) checkoutO;

        Boolean validShippingSession = true;
        Boolean validPaymentSession = true;

        FlowMessage completionMessage = completionFlow.run(session,optional.get());
        if (!completionMessage.isSolved()){
            log.info("Error found on CompletionFlow ("+completionMessage.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(completionMessage.getErrorMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        Checkout completedCheckout = (Checkout) completionMessage.getAttribute("checkout");

        log.info("Responding with HttpStatus.OK");
        return ResponseEntity.of(Optional.of(ApiResponse.success(completedCheckout)));
    }

    @PostMapping(value = "/checkout/create", consumes = "application/json")
    public ResponseEntity<ApiResponse> create(HttpSession session, @RequestBody  Checkout checkout){
        log.info("Creating Checkout for session id->"+session.getId());
        return getMapResponseEntity(creationFlow.run(checkout,session));
    }

    @PostMapping("/checkout/complete")
    public ResponseEntity<ApiResponse> complete(HttpSession session, Principal principal){
        log.info("Completing checkout for session id ->"+session.getId());
        Optional<Customer> optional = customerReplicationRepository.findByUsername(principal.getName());
        Object checkoutO = session.getAttribute("CHECKOUT");
        if (checkoutO==null){
            log.warn("Checkout not found on session!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Checkout checkout = (Checkout) checkoutO;

        Boolean validShippingSession = true;
        Boolean validPaymentSession = true;

        //Boolean validShippingSession = shippingServiceBroker
          //      .validateShippingSession(checkout.getShippingInfo().getShippingSession());

        //Boolean validPaymentSession = paymentServiceBroker
          //      .validatePaymentSession(checkout.getPaymentInfo().getPaymentSession());

        if (!validPaymentSession){
            log.info("Not valid PaymentSession");
            return new ResponseEntity<>(ApiResponse.error("Not valid PaymentSession"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!validShippingSession){
            log.info("Not valid ShippingSession");
            return new ResponseEntity<>(ApiResponse.error("Not valid ShippingSession"),
                    HttpStatus.BAD_REQUEST);
        }

        FlowMessage message = completionFlow.run(session,optional.get());
        if (!message.isSolved()){
            log.info("Error found on CompletionFlow ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(message.getErrorMessage()),
                    HttpStatus.BAD_REQUEST);
        }

        Checkout checkout1 = (Checkout) message.getAttribute("checkout");
        log.info("Responding with HttpStatus.OK");
        return ResponseEntity.of(Optional.of(ApiResponse.success(checkout1)));
    }

    @PostMapping(value = "/checkout/update", consumes = "application/json")
    public ResponseEntity<ApiResponse> update(
            HttpSession session
            , @RequestBody Checkout checkout
            , @RequestParam(name = "attribute") CheckoutAttributeName attribute){

        log.info("Updating Checkout for session id '"+session.getId()+"'");
        log.info("Attribute name to change is '"+attribute.name()+"'");

        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        checkoutAttribute.setName(attribute);
        if (attribute==CheckoutAttributeName.CART)
            checkoutAttribute.setAttribute(checkout.getShoppingCart());
        if (attribute==CheckoutAttributeName.PAYMENT)
            checkoutAttribute.setAttribute(checkout.getPaymentInfo());
        if (attribute==CheckoutAttributeName.SHIPPING)
            checkoutAttribute.setAttribute(checkout.getShippingInfo());


        FlowMessage message = upgradeFlow.run(checkoutAttribute,session);

        if (!message.isSolved()){
            log.info("FlowMessage for Attribute Updating has errors ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(ApiResponse.error(message.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }
        Checkout c = (Checkout) message.getAttribute("checkout");

        log.info("Call to /checkout/update has been solved successfully!");
        return ResponseEntity.of(Optional.of(ApiResponse.success(c)));
    }

    @GetMapping("/checkout")
    public ResponseEntity<Map<String,Object>> getMyCheckoutOnSession(HttpSession session){
        log.info("Retrieving current Checkout from HttpSession");
        Map<String,Object> response = new HashMap<>();
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
        response.put("response",checkout);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/dev/checkout/generate")
    public ResponseEntity<Map<String, Object>> generateCheckout(HttpSession session){
        log.info("'Generate Checkout' Developer Profile end point called");
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")){
            log.info("Profile accepted");
            log.info("Generating a Checkout instance on Session for testing...");
            Checkout checkout = dataGenerator.generateCheckout();
            PaymentInfo paymentInfo = checkout.getPaymentInfo();
            ShippingInfo shippingInfo = checkout.getShippingInfo();
            ShoppingCart shoppingCart = checkout.getShoppingCart();
            try {
                creationFlow.run(checkout,session);
                checkout.setPaymentInfo(paymentInfo);
                checkout.setShoppingCart(shoppingCart);
                checkout.setShippingInfo(shippingInfo);

                CheckoutAttribute shoppingCartCheckoutAttribute = CheckoutAttribute
                        .builder()
                        .attribute(checkout.getShoppingCart())
                        .name(CheckoutAttributeName.CART)
                        .build();

                upgradeFlow.run(shoppingCartCheckoutAttribute,session);
                checkout.setPaymentInfo(paymentInfo);
                checkout.setShippingInfo(shippingInfo);

                log.info("Calling Shipping Service to get Shipping Summary");
                ShippingBrokerResponse brokerResponse =
                shippingServiceBroker.requestShippingSummary(shippingInfo.getCustomerPosition(),shippingInfo.getStorePosition());
                ShippingSummary summary = (ShippingSummary) brokerResponse.getMessages().get("summary");

                shippingInfo.setShippingSession(summary.getShippingFee().getTemporalAccessToken().getBase64Encoding());
                shippingInfo.setFee(summary.getShippingFee().getFee());

                checkout.setShippingInfo(shippingInfo);

                CheckoutAttribute shippingInfoCheckoutAttribute = CheckoutAttribute
                        .builder()
                        .attribute(checkout.getShippingInfo())
                        .name(CheckoutAttributeName.SHIPPING)
                        .build();

                upgradeFlow.run(shippingInfoCheckoutAttribute,session);
                checkout.setPaymentInfo(paymentInfo);

                CheckoutAttribute paymentInfoCheckoutAttribute = CheckoutAttribute
                        .builder()
                        .attribute(checkout.getPaymentInfo())
                        .name(CheckoutAttributeName.PAYMENT)
                        .build();

                upgradeFlow.run(paymentInfoCheckoutAttribute,session);

                Checkout checkout1 = (Checkout) session.getAttribute("CHECKOUT");

                Map<String,Object> response = new HashMap<>();
                response.put("response",checkout1);
                log.info("Checkout generated on Session successfully");
                return new ResponseEntity<>(response,HttpStatus.OK);

            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        log.info("This endpoint is callable only if current profile is 'dev'");
        return new ResponseEntity<>(HttpStatus.GONE);
    }

    //----------------------------------- PRIVATE METHODS ------------------------------------------------

    private ResponseEntity<Map<String,Object>> getResponseEntityByPageable(Page page){
        try {
            if (page==null){
                throw  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No checkouts found");
            }
            List<Checkout> checkouts = page.getContent();
            Map<String,Object> response = new HashMap<>();
            response.put("checkouts",checkouts);
            response.put("page",page.getNumber());
            response.put("pages",page.getTotalPages());
            log.info("Ok");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            log.info("Error");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    private ResponseEntity<ApiResponse> getMapResponseEntity(FlowMessage run) {
        FlowMessage message = run;
        if (!message.isSolved()){
            log.info("FlowMessage for Updating has errors ("+message.getErrorMessage()+")");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Checkout checkout = (Checkout) message.getAttribute("checkout");
        log.info("Responding with HttpStatus.OK");
        return ResponseEntity.of(Optional.of(ApiResponse.success(checkout)));
    }


}
