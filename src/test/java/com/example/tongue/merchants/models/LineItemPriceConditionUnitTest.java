package com.example.tongue.merchants.models;

import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.shopping.LineItemPriceCondition;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LineItemPriceConditionUnitTest {

    @Test
    public void shouldAccomplishedReturnTrueWhenLeq500Heq100AndProductPrice400(){
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(400));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(100));
        condition.setLeq(BigDecimal.valueOf(500));
        assertTrue(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenLeq500Heq100AndProductPrice700(){
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(700));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(100));
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenLeq500Heq1000AndProductPrice700(){
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(700));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(1000));
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenLeq500Heq1000AndProductPrice100(){
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(1000));
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenLeq500Heq1000AndProductPrice1200(){
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1200));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(1000));
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenLeq500Eq1000AndProductPrice1000() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1000));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnTrueWhenLeq500Eq100AndProductPrice100() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setLeq(BigDecimal.valueOf(500));
        assertTrue(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenProductPriceLessThanZero() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-100));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setLeq(BigDecimal.valueOf(500));
        assertFalse(condition.accomplishedBy(product));
    }

    @Test
    public void shouldAccomplishedReturnFalseWhenProductPriceLessThanZeroAndHeqNegative200() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-100));
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setHeq(BigDecimal.valueOf(-200));
        assertFalse(condition.accomplishedBy(product));
    }

}