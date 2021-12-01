package com.example.tongue.checkout.flows;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.ValidationResponse;
import com.example.tongue.locations.models.Location;
import com.example.tongue.merchants.models.Product;
import com.example.tongue.merchants.models.StoreVariant;
import com.example.tongue.merchants.repositories.ProductRepository;
import com.example.tongue.merchants.repositories.StoreVariantRepository;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
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
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        item1.setProduct(product);
        cart.addItem(item1);
        Location destination = new Location();
        Location origin = new Location();
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldRejectCheckoutWhenProductIdNot100(){
        Checkout checkout = new Checkout();
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(101L);
        item1.setProduct(product);
        cart.addItem(item1);
        Location destination = new Location();
        Location origin = new Location();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setCart(cart);
        checkout.setStoreVariant(storeVariant);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldAcceptCheckoutWhenProductIdIs100(){
        Checkout checkout = new Checkout();
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        cart.addItem(item1);
        Location destination = new Location();
        Location origin = new Location();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setCart(cart);
        checkout.setStoreVariant(storeVariant);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertTrue(response.isSolved());
    }

    @Test
    public void shouldRejectCheckoutWhenCartIsEmpty(){
        Checkout checkout = new Checkout();
        Location destination = new Location();
        Location origin = new Location();
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setStoreVariant(storeVariant);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertFalse(response.isSolved());
    }

    @Test
    public void shouldReturnMessageWhenOriginHasNoLatitude(){
        String expected = "Origin location attributes must be populated";
        Checkout checkout = new Checkout();
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        cart.addItem(item1);
        Location destination = new Location();
        Location origin = new Location();
        origin.setLongitude(2.222F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setCart(cart);
        checkout.setStoreVariant(storeVariant);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        assertEquals(expected,response.getErrorMessage());

    }

    @Test
    public void shouldHasNoErrorMessageWhenCheckoutIsValid(){
        Checkout checkout = new Checkout();
        Cart cart = new Cart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(100L);
        item1.setProduct(product);
        cart.addItem(item1);
        Location destination = new Location();
        Location origin = new Location();
        origin.setLatitude(3.33F);
        origin.setLongitude(2F);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setCart(cart);
        checkout.setStoreVariant(storeVariant);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        ValidationResponse response =
                checkoutValidation.softValidation(checkout);
        String errorMessage = response.getErrorMessage();
        assertNull(errorMessage);
    }

}