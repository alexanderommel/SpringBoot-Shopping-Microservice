package com.example.tongue.core.converters;

import com.example.tongue.domain.checkout.CheckoutAttribute;
import com.example.tongue.core.exceptions.JsonBadFormatException;
import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.domain.shopping.LineItem;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.*;

public class CheckoutAttributeConverterUnitTest {

    private CheckoutAttributeConverter converter;

    @Before
    public void setUp(){
        converter = new CheckoutAttributeConverter();
    }

    @Test(expected = JsonBadFormatException.class)
    public void givenRandomStringThenThrowJsonBadFormatException(){
        String random = "abcdefg";
        converter.convert(random);
    }

    @Test(expected = ResponseStatusException.class)
    public void givenValidBabyObjectJsonWhenConvertingThenThrowResponseStatusException(){
        String json = "{\"baby\":\"str\"}";
        converter.convert(json);
    }

    @Test()
    public void givenCartCheckoutAttributeJsonWithBabyObjectThenReturnMessage(){
        String expected = "Object 'shoppingCart' is missing";
        String checkoutBabyJson = "{\"name\":\"CART\",\"baby\":\"string\"}";
        try{
            converter.convert(checkoutBabyJson);
        }catch (ResponseStatusException responseStatusException){
            assertEquals(expected,responseStatusException.getReason());
        }
    }

    @Test
    public void givenCartCheckoutAttributeJsonWithEmptyCartThenReturnMessage(){
        String expected = "Object 'shoppingCart' is missing";
        String checkoutNullCartJson = "{\"name\":\"CART\",\"shoppingCart\":null}";
        try{
            converter.convert(checkoutNullCartJson);
        }catch (ResponseStatusException responseStatusException){
            assertEquals(expected,responseStatusException.getReason());
        }
    }

    @Test
    public void givenCartCheckoutJsonWithBadCartObjectThenReturnMessage(){
        String expected = "Field 'shoppingCart' must be a json object";
        String checkoutNullCartJson = "{\"name\":\"CART\",\"shoppingCart\":\"mcloving\"}";
        try{
            converter.convert(checkoutNullCartJson);
        }catch (ResponseStatusException responseStatusException){
            assertEquals(expected,responseStatusException.getReason());
        }
    }

    @Test
    public void givenCartCheckoutJsonWithEmptyItemsThenReturnNotNullCheckoutAttribute(){
        String expected = "Field 'shoppingCart' must be a json object";
        String checkoutEmptyItemsJson = "{\"name\":\"CART\",\"shoppingCart\":{\"items\":[]}}";
        CheckoutAttribute checkoutAttribute = converter.convert(checkoutEmptyItemsJson);
        assertNotNull(checkoutAttribute);
    }

    @Test(expected = ClassCastException.class)
    public void givenCartCheckoutJsonWithEmptyItemsWhenCastingToLocationThenThrowClassException(){
        String expected = "Field 'shoppingCart' must be a json object";
        String checkoutEmptyItemsJson = "{\"name\":\"CART\",\"shoppingCart\":{\"items\":[]}}";
        CheckoutAttribute checkoutAttribute = converter.convert(checkoutEmptyItemsJson);
        Position location = (Position) checkoutAttribute.getAttribute();
    }

    @Test
    public void givenValidCartAttributeThenConvertedAttributeShouldBeEqual(){
        /** Expected **/
        ShoppingCart expected = new ShoppingCart();
        expected.setInstructions("Deliver it here");
        LineItem item = new LineItem();
        Product product = new Product();
        product.setId(7L);
        item.setQuantity(3);
        item.setProduct(product);
        expected.addItem(item);
        /** Test **/
        String validCartJson = "{" +
                "\"name\":\"CART\"," +
                "\"shoppingCart\":{" +
                "\"instructions\":\"Deliver it here\","+
                "\"items\":[" +
                "{\"product\":{\"id\":7},\"quantity\":3}"+
                "]" +
                "}" +
                "}";
        CheckoutAttribute attribute = converter.convert(validCartJson);
        ShoppingCart shoppingCart = (ShoppingCart) attribute.getAttribute();
        Boolean instructions
                = shoppingCart.getInstructions().equalsIgnoreCase(expected.getInstructions());
        Boolean productIds
                = shoppingCart.getItems().get(0).getProduct().getId().equals(
                        expected.getItems().get(0).getProduct().getId());
        Boolean quantities
                = shoppingCart.getItems().get(0).getQuantity()==expected.getItems().get(0).getQuantity();
        Boolean response = instructions && productIds && quantities;
        assertTrue(response);
    }

}