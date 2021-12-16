package com.example.tongue.merchants.models;

import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.enumerations.DiscountScope;
import com.example.tongue.domain.merchant.enumerations.DiscountType;
import com.example.tongue.domain.merchant.enumerations.ProductsScope;
import com.example.tongue.domain.shopping.Cart;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.domain.shopping.LineItemPriceCondition;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DiscountUnitTest {

    @Test
    public void givenNoEntitledProductAndGlobalScopeWhenValidatingProductThenTrue(){
        Product product = new Product();
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ALL);
        assertTrue(discount.validForProduct(product));
    }

    @Test
    public void givenEntitledProductAndGlobalScopeWhenValidatingProductThenTrue(){
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ALL);
        discount.addEntitledProduct(product2);
        assertTrue(discount.validForProduct(product1));
    }

    @Test
    public void givenEntitledProductAndEntitledScopeWhenValidatingProductThenFalse(){
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.addEntitledProduct(product2);
        assertFalse(discount.validForProduct(product1));
    }

    @Test
    public void givenEntitledProductAndEntitledScopeWhenValidatingProductThenTrue(){
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(1L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.addEntitledProduct(product2);
        assertTrue(discount.validForProduct(product1));
    }

    @Test
    public void givenLineItemPriceConditionAndEntitledScopeWhenValidatingProductThenTrue(){
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(200));
        Product product2 = new Product();
        product2.setId(1L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.addEntitledProduct(product2);
        LineItemPriceCondition itemPriceCondition = new LineItemPriceCondition();
        itemPriceCondition.setHeq(BigDecimal.valueOf(100));
        discount.setLineItemPriceCondition(itemPriceCondition);
        assertTrue(discount.validForProduct(product1));
    }

    @Test
    public void givenItemPriceConditionEntitledScopeAndNoProductPriceWhenValidatingProductThenFalse(){
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(1L);
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.addEntitledProduct(product2);
        LineItemPriceCondition itemPriceCondition = new LineItemPriceCondition();
        itemPriceCondition.setHeq(BigDecimal.valueOf(100));
        discount.setLineItemPriceCondition(itemPriceCondition);
        assertFalse(discount.validForProduct(product1));
    }

    @Test
    public void shouldValidForCartReturnFalseWhenProductNotEntitled(){
        /** To validate**/
        Cart cart = new Cart();
        LineItem item = new LineItem();
        Product product = new Product(); product.setId(2L); product.setPrice(BigDecimal.ONE);
        item.setProduct(product); item.getPrice().setFinalPrice(product.getPrice());
        cart.addItem(item);
        /** Discount instantiation**/
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPrice(BigDecimal.valueOf(20.0));
        Discount discount = new Discount();
        discount.setProductsScope(ProductsScope.ENTITLED_ONLY);
        discount.setEntitledProducts(Arrays.asList(product1));
        Boolean current = discount.validForCart(cart);
        assertFalse(current);
    }

    @Test
    public void givenNoEntitledCartItemsWhenDiscountProductScopeIsGlobalAndNoEmptyEntitledThenOk(){

        Cart cart = new Cart();

        LineItem item1 = new LineItem(); item1.setQuantity(2);
        Product product1 = new Product(); product1.setId(1L);
        item1.setProduct(product1);

        cart.addItem(item1);

        Product product2 = new Product(); product2.setId(2L);
        Product product3 = new Product(); product3.setId(3L);

        Discount discount = new Discount();
        discount.addEntitledProduct(product2);
        discount.addEntitledProduct(product3);
        discount.setProductsScope(ProductsScope.ALL);
        discount.setDiscountScope(DiscountScope.LINE_ITEMS);
        discount.setDiscountType(DiscountType.PRODUCT);

        assertTrue(discount.validForCart(cart));


    }

}