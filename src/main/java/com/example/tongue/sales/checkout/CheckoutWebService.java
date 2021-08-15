package com.example.tongue.sales.checkout;

import com.example.tongue.core.converters.CheckoutAttributeConverter;
import com.example.tongue.merchants.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CheckoutWebService {

    private CheckoutRepository checkoutRepository;

    public CheckoutWebService(@Autowired CheckoutRepository checkoutRepository){
        this.checkoutRepository = checkoutRepository;
    }


    @GetMapping(value = "/checkouts",params = {"page","size"})
    public ResponseEntity<Map<String,Object>> all(@RequestParam(defaultValue = "0",required = false) int page
            , @RequestParam(defaultValue = "50",required = false) int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Checkout> checkoutPage = checkoutRepository.findAll(pageable);
        return getResponseEntityByPageable(checkoutPage);
    }


    /**
     * Only origin and storeVariant must be populated
     * It creates a first checkout instance inside user's session
     * @param session
     * @param checkout
     * @return
     */

    @GetMapping("/checkouts/create")
    public ResponseEntity<Map<String,Object>> create(HttpSession session, Checkout checkout){
        Map<String,Object> response = new HashMap<>();

        CheckoutRequestValidationFilter validationFilter
                = new CheckoutRequestValidationFilter(CheckoutValidationType.SIMPLE);
        CheckoutDiscountsLoadingFilter discountsLoadingFilter
                = new CheckoutDiscountsLoadingFilter();
        CheckoutSessionFilter sessionFilter
                = new CheckoutSessionFilter(session);
        List<CheckoutFilter> filters = new ArrayList<>();
        filters.add(validationFilter);
        filters.add(discountsLoadingFilter);
        filters.add(sessionFilter);
        CheckoutFluxDefinition fluxDefinition = new CheckoutFluxDefinition(filters);
        Checkout checkout1 = fluxDefinition.filter(checkout);

        response.put("response",checkout1);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }


    @PostMapping(value = "/checkouts/update")
    public ResponseEntity<Map<String,Object>>
    update(HttpSession session, @RequestBody  String attribute){

            Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
            CheckoutAttributeConverter converter = new CheckoutAttributeConverter();
            CheckoutAttribute conversion =
                    (CheckoutAttribute) converter.convert(attribute,null,null);
            Map<String,Object> response = new HashMap<>();
            response.put("response",conversion);
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
