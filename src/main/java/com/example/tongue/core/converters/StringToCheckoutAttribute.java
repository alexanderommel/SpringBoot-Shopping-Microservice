package com.example.tongue.core.converters;

import com.example.tongue.locations.models.Location;
import com.example.tongue.sales.checkout.CheckoutAttribute;
import com.example.tongue.sales.checkout.CheckoutAttributeName;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.*;
import java.util.stream.Collectors;

public class StringToCheckoutAttribute  implements Converter<String,CheckoutAttribute> {

    private Location unwrapLinkedHashToLocation(LinkedHashMap hashMap){
        Location location = new Location();
        String placeId = (String) hashMap.get("googlePlaceId");
        if (placeId==null) return null;
        location.setGooglePlaceId(placeId);
        return location;
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
    
    @Override
    public CheckoutAttribute convert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        try{

            // Map JSON attributes to map keys
            Map<?,?> map = objectMapper.readValue(s,Map.class);
            String name = (String) mapGetIgnoringCase(map,"name");
            if (name==null) return null;

            // Get checkout enumerations
            Set<String> enums =
                    EnumSet.allOf(CheckoutAttributeName.class).
                            stream().map(CheckoutAttributeName::name).
                            collect(Collectors.toSet());

            // Assignations
            if (enums.contains(name)){

                // Populate checkout attribute with destination
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.DESTINATION))){
                    checkoutAttribute.setName(CheckoutAttributeName.DESTINATION);
                    LinkedHashMap hashMap = (LinkedHashMap) mapGetIgnoringCase(map,"destination");
                    if (hashMap==null) return null;
                    Location location = unwrapLinkedHashToLocation(hashMap);
                    if (location==null) return null;
                    checkoutAttribute.setAttribute(location);
                    return checkoutAttribute;
                }

                // Populate checkout attribute with origin

                // Populate checkout attribute with cart

                // Populate checkout attribute with payment

            }
        } catch (JsonProcessingException exception){
            return null;
        }
        return null;
    }
}
