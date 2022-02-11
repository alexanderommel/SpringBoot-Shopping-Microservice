package com.example.tongue.checkout.services;

import com.example.tongue.domain.checkout.Checkout;
import com.example.tongue.domain.checkout.ShippingInfo;
import com.example.tongue.domain.checkout.ValidationResponse;
import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.services.CheckoutValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@DisplayName("Checkout Soft Validation Unit Testing")
@RunWith(MockitoJUnitRunner.class)
public class CheckoutSoftValidationUnitTest {


    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreVariantRepository storeVariantRepository;

    @InjectMocks
    private CheckoutValidation checkoutValidation;

    @BeforeAll
    public void setUp(){
        this.checkoutValidation = new CheckoutValidation();
    }

    @Before
    public void mockWhen(){
        Mockito.when(productRepository.existsById(100L)).thenReturn(true);
        Mockito.when(storeVariantRepository.existsById(1L)).thenReturn(true);

    }

    @Test
    public void shouldRejectIncompleteCheckoutWhenValidationIsSoft(){
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        Position destination = new Position();
        Position origin = new Position();
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldRejectCheckoutWhenProductIdNot100(){
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(101L);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        Position destination = new Position();
        Position origin = new Position();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setShoppingCart(shoppingCart);
        checkout.setStoreVariant(storeVariant);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldAcceptCheckoutWhenProductIdIs100(){
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        Position destination = new Position();
        Position origin = new Position();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setShoppingCart(shoppingCart);
        checkout.setStoreVariant(storeVariant);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertTrue(response.isSolved());
    }

    @Test
    public void shouldRejectCheckoutWhenCartIsEmpty(){
        Checkout checkout = new Checkout();
        Position destination = new Position();
        Position origin = new Position();
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setStoreVariant(storeVariant);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldReturnMessageWhenOriginHasNoLatitude(){
        String expected = "Origin position attributes must be populated";
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        Position destination = new Position();
        Position origin = new Position();
        origin.setLongitude(2.222F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setShoppingCart(shoppingCart);
        checkout.setStoreVariant(storeVariant);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertEquals(expected,response.getErrorMessage());

    }

    @Test
    public void shouldHasNoErrorMessageWhenCheckoutIsValid(){
        Checkout checkout = new Checkout();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        Position destination = new Position();
        Position origin = new Position();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setShoppingCart(shoppingCart);
        checkout.setStoreVariant(storeVariant);
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(origin).storePosition(destination).build();
        checkout.setShippingInfo(shippingInfo);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        String errorMessage = response.getErrorMessage();
        assertNull(errorMessage);
    }

}