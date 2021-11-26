package com.example.tongue.checkout.webservices;

import com.example.tongue.checkout.filters.*;
import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.CheckoutAttribute;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.checkout.repositories.CheckoutRepository;
import com.example.tongue.core.converters.CheckoutAttributeConverter;
import com.example.tongue.locations.repositories.LocationRepository;
import com.example.tongue.merchants.repositories.DiscountRepository;
import com.example.tongue.merchants.repositories.ModifierRepository;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.shopping.models.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CheckoutWebService {

    private CheckoutRepository checkoutRepository;
    private CheckoutRequestChain requestChain;
    private CheckoutUpdateChain updateChain;
    private StoreVariantRepository storeVariantRepository;
    private ProductRepository productRepository;
    private DiscountRepository discountRepository;
    private ModifierRepository modifierRepository;
    private LocationRepository locationRepository;
    private CheckoutCompletionFlow completionFlow;
    private CheckoutCreationFlow creationFlow;
    private CheckoutUpgradeFlow upgradeFlow;

    public CheckoutWebService(@Autowired CheckoutRepository checkoutRepository,
                              @Autowired StoreVariantRepository storeVariantRepository,
                              @Autowired ProductRepository productRepository,
                              @Autowired DiscountRepository discountRepository,
                              @Autowired ModifierRepository modifierRepository,
                              @Autowired LocationRepository locationRepository,
                              @Autowired CheckoutCompletionFlow completionFlow,
                              @Autowired CheckoutCreationFlow creationFlow,
                              @Autowired CheckoutUpgradeFlow upgradeFlow){

        this.checkoutRepository = checkoutRepository;
        this.storeVariantRepository = storeVariantRepository;
        this.productRepository=productRepository;
        this.discountRepository=discountRepository;
        this.modifierRepository=modifierRepository;
        this.completionFlow=completionFlow;
        this.upgradeFlow=upgradeFlow;
        this.creationFlow=creationFlow;
        this.locationRepository=locationRepository;
        this.requestChain = new CheckoutRequestChain(storeVariantRepository,
                productRepository,
                modifierRepository,
                discountRepository);
        this.updateChain = new CheckoutUpdateChain(storeVariantRepository,
                productRepository,
                discountRepository,
                modifierRepository,
                locationRepository,
                checkoutRepository);
    }


    @GetMapping(value = "/checkouts",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Checkout> checkoutPage = checkoutRepository.findAll(pageable);
        return getResponseEntityByPageable(checkoutPage);
    }


    @GetMapping("/checkout/create")
    public ResponseEntity<Map<String,Object>> create(HttpSession session, @RequestBody  Checkout checkout){
        Map<String,Object> response = new HashMap<>();
        Checkout checkout1 = this.requestChain.doFilter(checkout,session);
        response.put("response",checkout1);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }

    @GetMapping("/checkout/create/v2")
    public ResponseEntity<Map<String,Object>> createV2(HttpSession session, @RequestBody  Checkout checkout){
        Map<String,Object> response = new HashMap<>();
        return getMapResponseEntity(response, creationFlow.run(checkout,session));
    }

    @GetMapping("/checkout/complete/v2")
    public ResponseEntity<Map<String,Object>> complete(HttpSession session){
        Map<String,Object> response = new HashMap<>();
        FlowMessage message = completionFlow.run(session);
        if (!message.isSolved()){
            response.put("error",message.getErrorMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        Order order = (Order) message.getAttribute("order");
        response.put("response",order);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping(value = "/checkout/update/v2")
    public ResponseEntity<Map<String,Object>> updateV2(
            HttpSession session, @RequestBody  String attribute){

        Map<String,Object> response = new HashMap<>();
        CheckoutAttributeConverter converter = new CheckoutAttributeConverter();
        CheckoutAttribute checkoutAttribute =
                (CheckoutAttribute) converter.convert(attribute,null,null);
        return getMapResponseEntity(response, upgradeFlow.run(checkoutAttribute, session));
    }

    @NotNull
    private ResponseEntity<Map<String, Object>> getMapResponseEntity(Map<String, Object> response, FlowMessage run) {
        FlowMessage message = run;
        if (!message.isSolved()){
            response.put("error",message.getErrorMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Checkout checkout = (Checkout) message.getAttribute("checkout");
        response.put("response",checkout);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PostMapping(value = "/checkout/update")
    public ResponseEntity<Map<String,Object>>
    update(HttpSession session, @RequestBody  String attribute){
        CheckoutAttributeConverter converter = new CheckoutAttributeConverter();
        CheckoutAttribute checkoutAttribute =
                    (CheckoutAttribute) converter.convert(attribute,null,null);
        Checkout checkout = this.updateChain.doFilter(checkoutAttribute,session);
        Map<String,Object> response = new HashMap<>();
        response.put("response",checkout);
        return new ResponseEntity<>(response,HttpStatus.OK);
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
