package com.example.tongue.core.converters;

import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.domain.shopping.LineItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LinkedHashMapConverter {

    public static Position toLocation(LinkedHashMap hashMap){
        Position location = new Position();
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
        // End instructions population



        // Modifiers population is self-optional (Validated on further step)
        Object modifiersHashes = hashMap.get("modifiers");
        if (modifiersHashes!=null){
            if (modifiersHashes instanceof List){
                List<LinkedHashMap> linkedHashMaps = (List<LinkedHashMap>) modifiersHashes;
                List<Modifier> modifiers = new ArrayList<>();
                for (LinkedHashMap hash: linkedHashMaps) {
                    Modifier modifier = toModifier(hash);
                    if (item==null)
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Nested object 'modifier' must be a valid Modifier object");
                    modifiers.add(modifier);
                }
                System.out.println("MODIFIER BEING INSERTED ON LINE ITEM");
                item.setModifiers(modifiers);
            }else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Attribute 'modifiers' must be a list of nested Modifier objects");
        }


        // Modifiers population end


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
        // End Product population



        // Discount population
        Object discount = hashMap.get("discount");
        if (discount!=null){
            if (discount instanceof LinkedHashMap){
                item.setDiscount(toDiscount((LinkedHashMap) discount));
            }else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Nested object 'discount' must be a valid Discount object");
        }
        // End discount population

        return item;
    }

    public static Modifier toModifier(LinkedHashMap hashMap){
        Modifier modifier = new Modifier();
        Object id = hashMap.get("id");
        if (id==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Modifier 'id' field is mandatory");
        if (id instanceof Number){
            modifier.setId(((Number) id).longValue());
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'id' must be an integer");
        return modifier;
    }

    public static ShoppingCart toCart(LinkedHashMap hashMap){
        ShoppingCart shoppingCart = new ShoppingCart();
        Boolean ignoreProductDiscount=Boolean.FALSE;

        // Instructions attribute population (optional)
        Object instructions = hashMap.get("instructions");
        if (instructions!=null){
            if (instructions instanceof String)
                shoppingCart.setInstructions((String) instructions);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Attribute 'instructions' must be a string");
        }

        // Discount attribute population (optional)
        Object discount = hashMap.get("discount");
        if (discount!=null){
            if (discount instanceof LinkedHashMap){
                ignoreProductDiscount=Boolean.TRUE;
                shoppingCart.setDiscount(toDiscount((LinkedHashMap) discount));
            }else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Nested object 'discount' must be a valid Discount object");
        }

        // Items population (not null)
        Object hashMaps =  hashMap.get("items");
        if (hashMaps==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ShoppingCart line items must not be empty");
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
            shoppingCart.setItems(items);
        }else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Attribute 'items' must be a list of nested LineItem objects");
        return shoppingCart;
    }
}
