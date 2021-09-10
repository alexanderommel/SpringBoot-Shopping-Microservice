package com.example.tongue.orders;

import com.example.tongue.merchants.enumerations.ValueType;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.sales.models.Cart;
import com.example.tongue.sales.models.LineItem;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;

public class CartUnitTest {
    @Test
    public void testUpdatePriceWithGlobalDiscount(){
        Cart cart = new Cart();
        Discount discount = new Discount();
        discount.setValueType(ValueType.FIXED_AMOUNT);
        discount.setValue(BigDecimal.valueOf(15.0));
        cart.setDiscount(discount);
        // Products
        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(10.0));
        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf(20.0));
        Product product3 = new Product();
        product3.setPrice(BigDecimal.valueOf(30.0));
        // Line items
        LineItem item1 = new LineItem();
        item1.setQuantity(2);
        item1.setProduct(product1);
        LineItem item2 = new LineItem();
        item2.setQuantity(2);
        item2.setProduct(product2);
        LineItem item3 = new LineItem();
        item3.setQuantity(2);
        item3.setProduct(product3);
        // Items
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item3);
        // TEST
        System.out.println("Testing Cart updatePrice() method when discount is provided at cart level");
        cart.updatePrice();
        System.out.println("Cart final price is: "+cart.getPrice().getFinalPrice());
        assert 105.0 == cart.getPrice().getFinalPrice().doubleValue(): "Test failure";

    }
    @Test
    public void testUpdatePriceWithLineDiscount(){
        Cart cart = new Cart();
        // Products
        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(10.0));
        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf(20.0));
        Product product3 = new Product();
        product3.setPrice(BigDecimal.valueOf(30.0));
        // Line items
        LineItem item1 = new LineItem();
        item1.setQuantity(2);
        item1.setProduct(product1);
        LineItem item2 = new LineItem();
        item2.setQuantity(2);
        item2.setProduct(product2);
        LineItem item3 = new LineItem();
        item3.setQuantity(2);
        item3.setProduct(product3);
        Discount discount1 = new Discount();
        discount1.setValueType(ValueType.FIXED_AMOUNT);
        discount1.setValue(BigDecimal.valueOf(10.0));
        Discount discount3 = new Discount();
        discount3.setValueType(ValueType.PERCENTAGE);
        discount3.setValue(BigDecimal.valueOf(10.0));
        item3.setDiscount(discount3);
        item1.setDiscount(discount1);
        // Items
        cart.addItem(item1);
        cart.addItem(item2);
        cart.addItem(item3);
        // TEST
        System.out.println("Testing Cart updatePrice() method when discount is not provided at cart level");
        cart.updatePrice();
        System.out.println("Cart final price is: "+cart.getPrice().getFinalPrice());
        assert 94.0 == cart.getPrice().getFinalPrice().doubleValue(): "Test failure";
    }
}
