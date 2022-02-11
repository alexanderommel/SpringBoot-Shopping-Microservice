package com.example.tongue.checkout.services;

import com.example.tongue.core.domain.Position;
import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.integration.shipping.ShippingBroker;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.integration.shipping.ShippingSummary;
import com.example.tongue.domain.merchant.Modifier;
import com.example.tongue.domain.merchant.Product;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.DiscountRepository;
import com.example.tongue.repositories.merchant.ModifierRepository;
import com.example.tongue.repositories.merchant.ProductRepository;
import com.example.tongue.domain.shopping.CartPrice;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.services.CheckoutSession;
import com.example.tongue.services.CheckoutUpgradeFlow;
import com.example.tongue.services.CheckoutValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutUpgradeFlowTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private ModifierRepository modifierRepository;
    @Mock
    private CheckoutValidation checkoutValidation;
    @Mock
    private CheckoutSession checkoutSession;
    @Mock
    private ShippingBroker shippingBroker;
    @InjectMocks
    private CheckoutUpgradeFlow upgradeFlow;

    @Before
    public void setUp(){
        /** In Session Checkout Mocking**/
        Checkout checkout = new Checkout();
        Position origin = Position.builder().latitude(1333F).longitude(552.2F).build();
        ShippingInfo shippingInfo = ShippingInfo.builder().customerPosition(null).storePosition(null).build();
        checkout.setShippingInfo(shippingInfo);
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(1L);
        checkout.setStoreVariant(storeVariant);
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(2L);
        item1.setQuantity(2);
        item1.setProduct(product);
        item1.setInstructions("Test instruction");
        shoppingCart.addItem(item1);
        checkout.setShoppingCart(shoppingCart);
        Mockito.when(checkoutSession.get(null)).thenReturn(checkout);
        /** Product Repository Mocking **/
        Product product1 = new Product(); product1.setId(1L);
        Product product2 = new Product(); product2.setId(2L);
        Product product3 = new Product(); product3.setId(3L);
        product1.setPrice(BigDecimal.valueOf(10));
        product2.setPrice(BigDecimal.valueOf(5));
        product3.setPrice(BigDecimal.valueOf(4.5));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        Mockito.when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        /** Broker Repository**/
        ShippingSummary summary = new ShippingSummary();
        summary.setFee(BigDecimal.valueOf(2.50));
        summary.setDistance(33.4);
        summary.setDeliveryTime(8.35);
        ShippingBrokerResponse brokerResponse = new ShippingBrokerResponse();
        brokerResponse.setSolved(true);
        brokerResponse.setStatusCode(200);
        brokerResponse.addMessage("summary",summary);
        Mockito.when(shippingBroker.
                requestShippingSummary(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(brokerResponse);
        /** CheckoutValidation Mocking**/
        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setSolved(true);
        Mockito.when(checkoutValidation.attributeValidation(ArgumentMatchers.any())).thenReturn(validationResponse);
        /** Modifier Repository Mocking**/
        Modifier modifier = new Modifier();
        modifier.setPrice(BigDecimal.valueOf(1.30));
        Mockito.when(modifierRepository.findById(555L)).thenReturn(Optional.of(modifier));
    }

    @Test
    public void givenEmptyAttributeThenReturnFalse(){
        FlowMessage flowMessage = upgradeFlow.run(null,null);
        assertFalse(flowMessage.isSolved());
    }

    @Test
    public void givenDestinationAttributeWhenRunningThenReturnTrue(){
        Position destination = Position.builder().latitude(15.0F).longitude(10.0F).build();
        CheckoutAttribute attribute = new CheckoutAttribute();
        attribute.setName(CheckoutAttributeName.DESTINATION);
        attribute.setAttribute(destination);
        FlowMessage flowMessage = upgradeFlow.run(attribute,null);
        assertTrue(flowMessage.isSolved());
    }

    @Test
    public void givenDestinationAttributeWhenRunningThenCheckoutDestinationIsEqual(){
        Position destination = Position.builder().latitude(15F).longitude(10.2F).build();
        CheckoutAttribute attribute = new CheckoutAttribute();
        attribute.setName(CheckoutAttributeName.DESTINATION);
        attribute.setAttribute(destination);
        FlowMessage flowMessage = upgradeFlow.run(attribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        Position current = checkout.getShippingInfo().getStorePosition();
        assertTrue(destination.equals(current));
    }

    @Test
    public void givenDestinationAttributeWhenRunningThenCheckoutProductPriceShouldNotBeUpdated(){
        int expected = BigDecimal.valueOf(5).intValue();
        Position destination = Position.builder().latitude(15F).longitude(10.2F).build();
        CheckoutAttribute attribute = new CheckoutAttribute();
        attribute.setName(CheckoutAttributeName.DESTINATION);
        attribute.setAttribute(destination);
        FlowMessage flowMessage = upgradeFlow.run(attribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertNotEquals(expected,checkout.getShoppingCart().getItems().get(0).getProduct().getPrice().intValue());
    }

    @Test
    public void givenSameSessionCartAttributeWhenRunningThenCheckoutProductPriceMustBe5(){
        int expected = BigDecimal.valueOf(5).intValue();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(2L);
        item1.setQuantity(2);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        checkoutAttribute.setName(CheckoutAttributeName.CART);
        checkoutAttribute.setAttribute(shoppingCart);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertEquals(expected,checkout.getShoppingCart().getItems().get(0).getProduct().getPrice().intValue());
    }

    @Test
    public void givenSameSessionCartAttributeWhenRunningThenCheckoutCartFinalPriceMustBeEqualsTo10(){
        int expected = BigDecimal.valueOf(10).intValue();
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        Product product = new Product();
        product.setId(2L);
        item1.setQuantity(2);
        item1.setProduct(product);
        shoppingCart.addItem(item1);
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        checkoutAttribute.setName(CheckoutAttributeName.CART);
        checkoutAttribute.setAttribute(shoppingCart);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertEquals(expected,checkout.getShoppingCart().getPrice().getFinalPrice().intValue());
    }

    @Test
    public void givenCartAttributeWhenRunningThenCheckoutFinalPriceMustBeEqualsToExpected(){
        /**  Fee is 2.50**/
        double expected = 27.5;
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        LineItem item2 = new LineItem();
        item1.setQuantity(2);
        Product product1 = new Product(); product1.setId(1L);
        Product product2 = new Product(); product2.setId(2L);
        item1.setProduct(product1);
        item2.setProduct(product2);
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        CheckoutAttribute checkoutAttribute =  new CheckoutAttribute();
        checkoutAttribute.setAttribute(shoppingCart);
        checkoutAttribute.setName(CheckoutAttributeName.CART);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertTrue(checkout.getPrice().getCheckoutTotal().equals(BigDecimal.valueOf(expected)));
    }

    @Test
    public void givenCartAttributeWithFinalPriceModifiedWhenRunningThenReturnedCheckoutFinalPriceNotEqual(){
        /**  Fee is 2.50**/
        double expected = 27.5;
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        LineItem item2 = new LineItem();
        item1.setQuantity(2);
        Product product1 = new Product(); product1.setId(1L);
        Product product2 = new Product(); product2.setId(2L);
        item1.setProduct(product1);
        item2.setProduct(product2);
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        CartPrice cartPrice = new CartPrice();
        cartPrice.setFinalPrice(BigDecimal.valueOf(1000.5));
        shoppingCart.setPrice(cartPrice);
        CheckoutAttribute checkoutAttribute =  new CheckoutAttribute();
        checkoutAttribute.setAttribute(shoppingCart);
        checkoutAttribute.setName(CheckoutAttributeName.CART);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertTrue(checkout.getPrice().getCheckoutTotal().equals(BigDecimal.valueOf(expected)));
    }

    @Test
    public void givenInvalidAttributeWhenRunningThenReturnFalse(){
        ValidationResponse response = new ValidationResponse();
        response.setSolved(false);
        Mockito.when(checkoutValidation.attributeValidation(ArgumentMatchers.any())).thenReturn(response);
        CheckoutAttribute checkoutAttribute = new CheckoutAttribute();
        Position destination = new Position();
        checkoutAttribute.setAttribute(destination);
        checkoutAttribute.setName(CheckoutAttributeName.DESTINATION);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        assertFalse(flowMessage.isSolved());
    }

    @Test
    public void givenCartAttributeWithModifiersWhenRunningThenCheckoutFinalPriceIsEqual(){
        double expected = 30.1; // 27.5 + 2.60
        /** Valid shoppingCart with basic attributes**/
        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = new LineItem();
        LineItem item2 = new LineItem();
        item1.setQuantity(2);
        Product product1 = new Product(); product1.setId(1L);
        Product product2 = new Product(); product2.setId(2L);
        item1.setProduct(product1);
        item2.setProduct(product2);
        /** Modifiers for just one product **/
        Modifier modifier = new Modifier();
        modifier.setId(555L); // On Database this modifier has a price of 1.30
        item1.addModifier(modifier);
        /** Add items to shoppingCart and run**/
        shoppingCart.addItem(item1);
        shoppingCart.addItem(item2);
        CheckoutAttribute checkoutAttribute =  new CheckoutAttribute();
        checkoutAttribute.setName(CheckoutAttributeName.CART);
        checkoutAttribute.setAttribute(shoppingCart);
        FlowMessage flowMessage = upgradeFlow.run(checkoutAttribute,null);
        Checkout checkout = (Checkout) flowMessage.getAttribute("checkout");
        assertTrue(checkout.getPrice().getCheckoutTotal().equals(BigDecimal.valueOf(expected)));
    }

}