package com.example.tongue.sales.checkout;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CheckoutWebService {

    private CheckoutFluxDefinition fluxDefinition;

    /**
     * Only origin and storeVariant must be populated
     * It creates a first checkout instance inside user's session
     * @param session
     * @param checkout
     * @return
     */

    @GetMapping("/checkouts/create")
    public ResponseEntity<Map<String,Object>> create(HttpSession session, Checkout checkout){
        try {
            CheckoutBindingMessage bindingMessage =
                    fluxDefinition.createCheckout(session,checkout);
            if (bindingMessage.hasErrors()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Map<String,Object> response = new HashMap<>();
            response.put("checkout",bindingMessage.getCheckout());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checkout attribute updating process is done one by one
     * @param session
     * @param checkoutAttribute
     */
    @PostMapping(value = "/checkouts/update")
    public void update(HttpSession session, CheckoutAttribute checkoutAttribute){
        Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
    }
}
