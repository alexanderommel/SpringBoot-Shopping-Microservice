package com.example.tongue.core.converters;

import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.sales.models.Cart;
import com.example.tongue.sales.models.LineItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LinkedHashMapConverter {

    public static Location toLocation(LinkedHashMap hashMap){
        Location location = new Location();
        Object placeId = hashMap.get("googlePlaceId");
        if (placeId==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Field 'googlePlaceId' is mandatory");
        if (placeId instanceof String){
            location.setGooglePlaceId((String) placeId);
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'googlePlaceId' must be a String");
        return location;
    }

    public static Discount toDiscount(LinkedHashMap hashMap){
        Discount discount = new Discount();
        Object id = hashMap.get("id");
        if (id==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Discount 'id' field is mandatory");
        if (id instanceof Number){
            discount.setId(((Number) id).longValue());
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'id' must be an integer");
        return discount;
    }

    public static Product toProduct(LinkedHashMap hashMap){
        Product product = new Product();
        Object id = hashMap.get("id");
        if (id==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product 'id' field is mandatory");
        if (id instanceof Number){
            product.setId(((Number) id).longValue());
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'id' must be an integer");
        return product;
    }

    public static LineItem toLineItem(LinkedHashMap hashMap, Boolean ignoreDiscount){
        LineItem item = new LineItem();

        // Quantity population is default 1 if not found
        Object quantity = hashMap.get("quantity");
        if (quantity==null){
            item.setQuantity(1);
        }
        else if (quantity instanceof Number){
            item.setQuantity((Integer) quantity);
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'quantity' must be an integer");

        // Instructions population is optional
        Object instructions = hashMap.get("instructions");
        if (instructions!=null){
            if (instructions instanceof String)
                item.setInstructions((String) instructions);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Attribute 'instructions' must be a string");
        }

        // Product population
        Object product = hashMap.get("product");
        if (product==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nested object 'product' is mandatory");
        }
        if (product instanceof LinkedHashMap){
            item.setProduct(toProduct((LinkedHashMap) product));
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nested object 'product' must be a valid Product object");

        if (ignoreDiscount){
            return item;
        }

        // Discount population
        Object discount = hashMap.get("discount");
        if (discount==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nested object 'discount' is mandatory");
        }
        if (discount instanceof LinkedHashMap){
            item.setDiscount(toDiscount((LinkedHashMap) discount));
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nested object 'discount' must be a valid Discount object");

        return item;
    }

    public static Cart toCart(LinkedHashMap hashMap){
        Cart cart = new Cart();
        Boolean ignoreProductDiscount=Boolean.FALSE;

        // Instructions attribute population (optional)
        Object instructions = hashMap.get("instructions");
        if (instructions!=null){
            if (instructions instanceof String)
                cart.setInstructions((String) instructions);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Attribute 'instructions' must be a string");
        }

        // Discount attribute population (optional)
        Object discount = hashMap.get("discount");
        if (discount!=null){
            if (discount instanceof LinkedHashMap){
                ignoreProductDiscount=Boolean.TRUE;
                cart.setDiscount(toDiscount((LinkedHashMap) discount));
            }else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Nested object 'discount' must be a valid Discount object");
        }

        // Items population (not null)
        Object hashMaps =  hashMap.get("items");
        if (hashMaps==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cart line items must not be empty");
        if (hashMaps instanceof List){
            List<LinkedHashMap> linkedHashMaps = (List<LinkedHashMap>) hashMaps;
            List<LineItem> items = new ArrayList<>();
            for (LinkedHashMap hash: linkedHashMaps) {
                LineItem item = toLineItem(hash,ignoreProductDiscount);
                if (item==null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Nested object 'item' must be a valid LineItem object");
                items.add(item);
            }
            cart.setItems(items);
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'items' must be a list of nested LineItem objects");
        return cart;
    }
}
