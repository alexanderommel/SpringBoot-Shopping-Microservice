package com.example.tongue.shopping.models;

import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.domain.merchant.enumerations.DiscountScope;
import com.example.tongue.domain.merchant.enumerations.DiscountType;
import com.example.tongue.domain.merchant.enumerations.ValueType;
import com.example.tongue.domain.merchant.Discount;
import com.example.tongue.domain.merchant.Product;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LineItemUnitTest {

    @Test
    public void shouldUpdateFinalPriceTo300SuccessfullyWhenProductPriceIs300(){
        BigDecimal expected = BigDecimal.valueOf(300.0);
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(300.0));
        LineItem lineItem = new LineItem();
        lineItem.setProduct(product);
        lineItem.updatePrice();
        assertEquals(expected,lineItem.getPrice().getFinalPrice());
    }

    @Test
    public void givenProductDiscountWhenUpdatePriceThenFinalPriceIs100(){
        BigDecimal expected = BigDecimal.valueOf(100);
        Discount discount = new Discount();
        discount.setDiscountType(DiscountType.PRODUCT);
        discount.setValueType(ValueType.FIXED_AMOUNT);
        discount.setValue(BigDecimal.valueOf(10));
        discount.setDiscountScope(DiscountScope.LINE_ITEMS);
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(110));
        LineItem lineItem = new LineItem();
        lineItem.setProduct(product);
        lineItem.setDiscount(discount);
        lineItem.updatePrice();
        BigDecimal current = lineItem.getPrice().getFinalPrice();
        assertEquals(expected.intValue(),current.intValue());
    }

}