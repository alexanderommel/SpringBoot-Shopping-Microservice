package com.example.tongue.orders;

import com.example.tongue.domain.merchant.enumerations.ProductsScope;
import com.example.tongue.domain.merchant.enumerations.ValueType;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.domain.shopping.LineItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CartUnitTest {

    @Test
    public void shouldUpdatePriceSuccessfullyWhenCartLevelDiscountIsProvided(){
        ShoppingCart shoppingCart = new ShoppingCart();
        Discount discount = new Discount();
        discount.setValueType(ValueType.FIXED_AMOUNT);
        discount.setValue(BigDecimal.valueOf(15.0));
        shoppingCart.setDiscount(discount);
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
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        shoppingCart.addItem(item3);
        // Method test
        shoppingCart.updatePrice();
        System.out.println("ShoppingCart final price is: "+ shoppingCart.getPrice().getFinalPrice());
        assert 105.0 == shoppingCart.getPrice().getFinalPrice().doubleValue(): "Test failure";

    }
    @Test
    public void shouldUpdatePriceSuccessfullyWhenCartLevelDiscountIsNotProvided(){
        ShoppingCart shoppingCart = new ShoppingCart();
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
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        shoppingCart.addItem(item3);
        // TEST
        shoppingCart.updatePrice();
        System.out.println("ShoppingCart final price is: "+ shoppingCart.getPrice().getFinalPrice());
        assert 94.0 == shoppingCart.getPrice().getFinalPrice().doubleValue(): "Test failure";
    }

    @Test
    public void shouldNotUpdatePriceWhenCartDiscountIsNotValidBecauseEntitlement(){
        /** The discount is not valid because one of the products
         * in the shopping shoppingCart is not entitled**/
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product1 = new Product(); product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(10.0));
        item1.setProduct(product1);
        LineItem item2 = new LineItem();
        Product product2 = new Product(); product2.setId(2L);
        product2.setPrice(BigDecimal.valueOf(20.0));
        item2.setProduct(product2);
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        //Discount instantiation
        Product product3 = new Product(); product3.setId(2L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.addEntitledProduct(product3);
        shoppingCart.setDiscount(discount);
        //Validation
        Boolean updated = shoppingCart.updatePrice();
        assertFalse(updated);
    }
}
