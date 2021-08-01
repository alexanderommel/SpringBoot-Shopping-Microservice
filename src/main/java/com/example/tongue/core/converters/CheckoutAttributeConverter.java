package com.example.tongue.core.converters;

import com.example.tongue.core.annotations.CartConversion;
import com.example.tongue.core.annotations.CheckoutAttributeConversion;
import com.example.tongue.core.exceptions.JsonBadFormatException;
import com.example.tongue.core.exceptions.OrderNotFoundException;
import com.example.tongue.locations.models.Location;
import com.example.tongue.sales.checkout.CheckoutAttribute;
import com.example.tongue.sales.checkout.CheckoutAttributeName;
import com.example.tongue.sales.models.Cart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CheckoutAttributeConverter implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor1) {
        System.out.println("TEST2");
        return true;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        System.out.println("TEST3333");
        return Collections.
                singleton(
                        new ConvertiblePair(String.class, CheckoutAttribute.class));
    }

    @Override
    public Object convert(Object o, TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor1) {
        System.out.println("TEST4");
        ObjectMapper objectMapper = new ObjectMapper();
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        System.out.println("TEST5");
        try {

            // Map JSON attributes to map keys
            Map<?,?> map = objectMapper.readValue((String)o,Map.class);
            String name = (String) mapGetIgnoringCase(map,"name");
            if (name==null)
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Field 'name' is missing");
            System.out.println("TEST6");
            // Get checkout attribute name enumeration constants
            Set<String> enums =
                    EnumSet.allOf(CheckoutAttributeName.class).
                            stream().map(CheckoutAttributeName::name).
                            collect(Collectors.toSet());

            // Assignations
            if (enums.contains(name)){
                System.out.println("TEST7");
                // Populate checkout attribute with destination
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.DESTINATION))){
                    checkoutAttribute.setName(CheckoutAttributeName.DESTINATION);
                    Object linkedHashMap = mapGetIgnoringCase(map,"destination");
                    if (linkedHashMap==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Object 'destination' is missing");
                    if (linkedHashMap instanceof LinkedHashMap){
                        LinkedHashMap hashMap = (LinkedHashMap) linkedHashMap;
                        Location location = LinkedHashMapConverter.toLocation(hashMap);
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
                        Location location = LinkedHashMapConverter.toLocation(hashMap);
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
                    }else
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Field 'cart' must be a json object");
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
        System.out.println("TEST8");
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
