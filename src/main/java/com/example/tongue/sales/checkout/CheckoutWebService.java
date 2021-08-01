package com.example.tongue.sales.checkout;

import com.example.tongue.core.annotations.CheckoutAttributeConversion;
import com.example.tongue.core.converters.CheckoutAttributeConverter;
import com.example.tongue.core.converters.StringToCheckoutAttribute;
import com.example.tongue.core.exceptions.JsonBadFormatException;
import com.example.tongue.core.exceptions.OrderNotFoundException;
import com.example.tongue.core.exceptions.ProductNotFoundException;
import com.example.tongue.locations.models.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping(value = "/checkouts/update")
    public ResponseEntity<Map<String,Object>> update(HttpSession session,
                                                     @CheckoutAttributeConversion @RequestBody  CheckoutAttribute attribute){

            Checkout checkout = (Checkout) session.getAttribute("CHECKOUT");
            //CheckoutAttributeConverter converter = new CheckoutAttributeConverter();
            //CheckoutAttribute conversion = (CheckoutAttribute) converter.convert(attribute,null,null);
            Map<String,Object> response = new HashMap<>();
            response.put("response","conversion");
            return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
