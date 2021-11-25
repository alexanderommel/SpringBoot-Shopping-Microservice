package com.example.tongue.discounts;

import com.example.tongue.merchants.enumerations.DiscountScope;
import com.example.tongue.merchants.enumerations.ProductsScope;
import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.LineItemPriceCondition;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.ValueSubtotalCondition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DiscountTests {

    public void test1(String hola){
        System.out.println(hola);
    }

    /*

    public void testDiscount1(){
        ArrayList<Product> cart = (ArrayList<Product>) buildCart();
        Discount discount = new Discount();
        discount.setId(1L);
        discount.setDescription("El descuento es valido para todos los productos en la tienda\n excepto para aquellos que cuestan mas de 20 dolares" +
                " y se descuenta un dolar a cada producto.\n El producto 3 no aplica para descuento");
        discount.setValue(BigDecimal.valueOf(1.0));
        discount.setProductsScope(ProductsScope.ALL);
        discount.setDiscountScope(DiscountScope.LINE_ITEMS);
        //PRICE CONDITION
        LineItemPriceCondition condition = new LineItemPriceCondition();
        condition.setLeq(BigDecimal.valueOf(20.0));
        discount.setLineItemPriceCondition(condition);
        //EXCEPCIONES
        List<Product> exceptions = new ArrayList<>();
        exceptions.add(cart.get(2));
        discount.setExcludedProducts(exceptions);
        System.out.println(discount.toString());
        System.out.println("Valid for cart: "+discount.validForCart(discount,cart));
        for (Product product:
             cart) {
            System.out.println(product.toString());
            System.out.println("Valid for this product: "+discount.validForProduct(discount,product));
        }
    }

    public void testDiscount2(){
        ArrayList<Product> cart = (ArrayList<Product>) buildCart();
        Discount discount = new Discount();
        discount.setId(2L);
        discount.setDescription("Este descuento le reduce 5 dolares a sus compras en los productos 1,2,4\n siempre y cuando usted" +
                "haya gastado al menos 25 dolares");
        discount.setValue(BigDecimal.valueOf(5.0));
        discount.setProductsScope(ProductsScope.ALL);
        discount.setDiscountScope(DiscountScope.SUBTOTAL);
        //ENTITLED
        ArrayList<Product> entitled = new ArrayList<>();
        entitled.add(cart.get(0));
        entitled.add(cart.get(1));
        entitled.add(cart.get(3));
        discount.setEntitledProducts(entitled);
        ValueSubtotalCondition condition = new ValueSubtotalCondition();
        condition.setHeq(BigDecimal.valueOf(25.0));
        //PRINTING
        System.out.println(discount.toString());
        System.out.println("Valid for cart: "+discount.validForCart(discount,cart));
        for (Product product:
                cart) {
            System.out.println(product.toString());
            System.out.println("Valid for this product: "+discount.validForProduct(discount,product));
        }
    }

    public List<Product> buildCart(){
        Product product1 = new Product();
        Product product2 = new Product();
        Product product3 = new Product();
        Product product4 = new Product();
        Product product5 = new Product();
        product1.setId(1L);
        product2.setId(2L);
        product3.setId(3L);
        product4.setId(4L);
        product5.setId(5L);
        product1.setPrice(BigDecimal.valueOf(10.5));
        product2.setPrice(BigDecimal.valueOf(15.5));
        product3.setPrice(BigDecimal.valueOf(5.5));
        product4.setPrice(BigDecimal.valueOf(25.5));
        product5.setPrice(BigDecimal.valueOf(30.5));
        product1.setTitle("Product1");
        product2.setTitle("Product2");
        product3.setTitle("Product3");
        product4.setTitle("Product4");
        product5.setTitle("Product5");
        List<Product> cart = new ArrayList<>();
        cart.add(product1);
        cart.add(product2);
        cart.add(product3);
        cart.add(product4);
        cart.add(product5);
        return cart;
    }

     */

}
