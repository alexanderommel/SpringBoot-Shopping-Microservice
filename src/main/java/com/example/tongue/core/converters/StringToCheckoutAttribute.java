package com.example.tongue.core.converters;

import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.sales.checkout.CheckoutAttribute;
import com.example.tongue.sales.checkout.CheckoutAttributeName;
import com.example.tongue.sales.models.Cart;
import com.example.tongue.sales.models.LineItem;
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

    private Cart unwrapLinkedHashMapToCart(LinkedHashMap hashMap){

        Cart cart = new Cart();
        Boolean cartLevelDiscount=Boolean.FALSE;

        // Instructions attribute population
        String instructions = (String) hashMap.get("instructions");
        if (instructions!=null) cart.setInstructions(instructions);

        // Discount attribute population
        LinkedHashMap discountMap = (LinkedHashMap) hashMap.get("discount");
        if (discountMap!=null){
            Discount discount = new Discount();
            Object object = discountMap.get("id");

            if (object!=null){
                Number number = (Number) object;
                Long id = number.longValue();
                discount.setId(id);
            }
            else
                discount=null;

            cart.setDiscount(discount);
            cartLevelDiscount=Boolean.TRUE;
        }

        // Line items population
        List<LinkedHashMap> itemsHashMaps = (List<LinkedHashMap>) hashMap.get("items");
        List<LineItem> items;
        if (itemsHashMaps!=null){

            // Line item population
            items = new ArrayList<>();
            for (LinkedHashMap linkedHashMap:itemsHashMaps) {
                LineItem item = new LineItem();

                // Product population
                LinkedHashMap productHash = (LinkedHashMap) linkedHashMap.get("product");
                if (productHash!=null){
                    Product product = new Product();
                    Object object = productHash.get("id");
                    if (object!=null){
                        Number number = (Number) object;
                        Long id = number.longValue();
                        product.setId(id);
                        item.setProduct(product);
                    }else continue;
                }else continue;

                // Product Discount population
                if (!cartLevelDiscount){
                    LinkedHashMap discountHash = (LinkedHashMap) linkedHashMap.get("discount");
                    if (discountHash!=null){
                        Discount discount = new Discount();
                        Object object = discountHash.get("id");
                        if (object!=null){
                            Number number = (Number) object;
                            Long id = number.longValue();
                            discount.setId(id);
                            item.setDiscount(discount);
                        }
                    }
                }

                // Quantity population
                Integer quantity = (Integer) linkedHashMap.get("quantity");
                if (quantity!=null){
                    item.setQuantity(quantity);
                }else {
                    item.setQuantity(1); //DEFAULT
                }

                // Line item population
                items.add(item);
            }
            if (!items.isEmpty()){
                cart.setItems(items);
            }
        }
        return cart;
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
                if (name.equalsIgnoreCase(String.valueOf(CheckoutAttributeName.CART))){
                    checkoutAttribute.setName(CheckoutAttributeName.CART);
                    LinkedHashMap hashMap = (LinkedHashMap) mapGetIgnoringCase(map,"cart");
                    if (hashMap==null) return null;
                    Cart cart = unwrapLinkedHashMapToCart(hashMap);
                    if (cart==null) return null;
                    checkoutAttribute.setAttribute(cart);
                    return checkoutAttribute;
                }

                // Populate checkout attribute with payment

            }
        } catch (JsonProcessingException exception){
            return null;
        }
        return null;
    }
}
