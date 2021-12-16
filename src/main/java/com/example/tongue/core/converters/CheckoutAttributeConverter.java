package com.example.tongue.core.converters;

import com.example.tongue.domain.checkout.CheckoutAttribute;
import com.example.tongue.domain.checkout.CheckoutAttributeName;
import com.example.tongue.core.exceptions.JsonBadFormatException;
import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.shopping.Cart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckoutAttributeConverter {

    public CheckoutAttribute convert(String o) {
        ObjectMapper objectMapper = new ObjectMapper();
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        try {

            // Map JSON attributes to map keys
            Map<?,?> map = objectMapper.readValue(o,Map.class);
            String name = (String) mapGetIgnoringCase(map,"name");
            if (name==null)
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Field 'name' is missing");
            // Get checkout attribute name enumeration constants
            Set<String> enums =
                    EnumSet.allOf(CheckoutAttributeName.class).
                            stream().map(CheckoutAttributeName::name).
                            collect(Collectors.toSet());

            // Assignations
            if (enums.contains(name)){
                // Populate checkout attribute with destination
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.DESTINATION))){
                    checkoutAttribute.setName(CheckoutAttributeName.DESTINATION);
                    Object linkedHashMap = mapGetIgnoringCase(map,"destination");
                    if (linkedHashMap==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Object 'destination' is missing");
                    if (linkedHashMap instanceof LinkedHashMap){
                        LinkedHashMap hashMap = (LinkedHashMap) linkedHashMap;
                        Position location = LinkedHashMapConverter.toLocation(hashMap);
                        checkoutAttribute.setAttribute(location);
                    }else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Field 'destination' must be a json object");
                    return checkoutAttribute;
                }

                // Populate checkout attribute with origin
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.ORIGIN))){
                    checkoutAttribute.setName(CheckoutAttributeName.ORIGIN);
                    Object linkedHashMap = mapGetIgnoringCase(map,"origin");
                    if (linkedHashMap==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Object 'origin' is missing");
                    if (linkedHashMap instanceof LinkedHashMap){
                        LinkedHashMap hashMap = (LinkedHashMap) linkedHashMap;
                        Position location = LinkedHashMapConverter.toLocation(hashMap);
                        checkoutAttribute.setAttribute(location);
                    }else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Field 'origin' must be a json object");
                    return checkoutAttribute;
                }

                // Populate checkout attribute with cart
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.CART))){
                    checkoutAttribute.setName(CheckoutAttributeName.CART);
                    Object linkedHashMap = mapGetIgnoringCase(map,"cart");
                    if (linkedHashMap==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Object 'cart' is missing");
                    if (linkedHashMap instanceof LinkedHashMap){
                        LinkedHashMap hashMap = (LinkedHashMap) linkedHashMap;
                        Cart cart = LinkedHashMapConverter.toCart(hashMap);
                        checkoutAttribute.setAttribute(cart);
                    }else{
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Field 'cart' must be a json object");
                    }
                    return checkoutAttribute;
                }

                // Populate checkout attribute with payment
                /* ON NEXT ITERATION
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.PAYMENT))){
                    checkoutAttribute.setName(CheckoutAttributeName.PAYMENT);
                    LinkedHashMap hashMap = (LinkedHashMap) mapGetIgnoringCase(map,"payment");
                    if (hashMap==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Object 'payment' is missing");
                }
                 */

            } else
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Field 'name' is not a valid constant, available constants are "+enums);


        } catch (JsonProcessingException exception) {
            throw new JsonBadFormatException();
        }
        return null;
    }

    private Object mapGetIgnoringCase(Map map, String key){

        // We assume that key is on uppercase
        Object object;
        object =  map.get(key);
        if (object==null){

            // Evaluate if its lowercase
            key = key.toLowerCase();
            object = map.get(key);
            if (object==null){

                // Capitalize
                key = key.substring(0, 1).toUpperCase() + key.substring(1);
                object = map.get(key);
                if (object==null)
                    return null;
            }
        }
        return object;
    }
}
