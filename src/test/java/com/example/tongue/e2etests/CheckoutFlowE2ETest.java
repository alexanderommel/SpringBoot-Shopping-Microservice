package com.example.tongue.e2etests;

import com.example.tongue.domain.checkout.*;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.domain.shopping.LineItem;
import com.example.tongue.domain.shopping.ShoppingCart;
import com.example.tongue.integration.payments.PaymentServiceBroker;
import com.example.tongue.integration.shipping.*;
import com.example.tongue.repositories.merchant.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
@AutoConfigureMockMvc
public class CheckoutFlowE2ETest {

    /**
     * Description: This test simulates the situation where a customer creates a Checkout and Completes it
     * Steps:
     * 1. Create a Checkout with the basic data
     * 2. Update the Shipping information
     * 3. Update the shopping cart (add 3 extra items)
     * 4. Update the shopping cart (remove the last item)
     * 5. Update the Shipping information with bad data
     * 6. Update the Payment info
     * 7. Remove the product modifiers from the shopping cart
     * 8. Add a product that belongs to a different store
     * 9. Complete the Checkout
     * **/

    @Autowired
    MockMvc mvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ConnectionFactory connectionFactory;
    @MockBean
    RabbitTemplate rabbitTemplate;
    @MockBean
    ShippingServiceBroker shippingServiceBroker;
    @MockBean
    PaymentServiceBroker paymentServiceBroker;

    @Autowired
    StoreVariantRepository storeVariantRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CollectionRepository collectionRepository;
    @Autowired
    GroupModifierRepository groupModifierRepository;
    @Autowired
    ModifierRepository modifierRepository;

    MockHttpSession httpSession = new MockHttpSession();

    Principal principal;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        Mockito.when(shippingServiceBroker.validateShippingSession(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(paymentServiceBroker.validatePaymentSession(ArgumentMatchers.anyString())).thenReturn(true);

        principal = Mockito.mock(Principal.class);

        Mockito.when(principal.getName()).thenReturn("bunny");
    }

    @Test
    public void sinceCustomerBeginsTheCheckoutUntilCompletesTheCheckout() throws Exception {

        log.info("End to End Test for Checkout Use Case");

        log.info("Mocked HttpSession id->"+httpSession.getId());



        StoreVariant s =
                storeVariantRepository.findAll().get(0);

        StoreVariant s2 =
                storeVariantRepository.findAll().get(1);
        Collection c1 =
                collectionRepository.findAllByStoreVariantId(s.getId()).get(0);
        List<Product> productList =
                productRepository.findAllByCollection_IdAndStatus(c1.getId(), ProductStatus.ACTIVE);
        List<GroupModifier> groupModifiers1 =
                groupModifierRepository.findAllByProduct_Id(productList.get(1).getId());
        List<Modifier> modifiers1 =
                modifierRepository.findAllByGroupModifier_Id(groupModifiers1.get(0).getId());

        ShoppingCart shoppingCart = new ShoppingCart();
        LineItem item1 = LineItem.builder()
                .product(productList.get(0))
                .quantity(2)
                .build();
        shoppingCart.getItems().add(item1);

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .customerPosition(Position.builder()
                        .latitude(3.219111F)
                        .longitude(-1.210111F)
                        .owner("Alexis")
                        .address("Quito")
                        .build())
                .build();


        Checkout checkout = Checkout.builder()
                .storeVariant(s)
                .shoppingCart(shoppingCart)
                .shippingInfo(shippingInfo)
                .build();

        /** Create a checkout instance **/

        MvcResult result = this.mvc.perform(get("/checkout/create")
                .contentType("application/json")
                .session(httpSession)
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());

        assertEquals(result.getResponse().getStatus(), 200 );

        /** Change customer position
         * We should get a ShippingSession in order to being able to complete the checkout
         * because on the last step of the Checkout flow, there's a shipping session validation.
         * There's other test that verifies the case where we don't provide a shipping session
         * Checkout Update Service assumes that the values related to the shipping are OK.
         * **/

        ShippingFee shippingFee = ShippingFee.builder()
                .fee(BigDecimal.valueOf(1.90))
                .temporalAccessToken(TemporalAccessToken.builder().base64Encoding("RandomSessionNumber").build())
                .build();

        ShippingSummary summary = ShippingSummary.builder()
                .arrivalTime(LocalTime.of(0,31))
                .distance(new Distance(28, Metrics.KILOMETERS))
                .shippingFee(shippingFee)
                .build();

        checkout.getShippingInfo().setCustomerPosition(Position.builder()
                .latitude(3.1111F)
                .longitude(-8.01112F)
                .owner("Alexander")
                .address("Calle Ruta 8")
                .build());

        checkout.getShippingInfo().setFee(summary.getShippingFee().getFee());
        checkout.getShippingInfo().setShippingSession(summary.getShippingFee().getTemporalAccessToken().getBase64Encoding());

        log.info("Customer updates Shipping Info");

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","SHIPPING")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200 );

        shoppingCart.getItems().add(LineItem.builder()
                .product(productList.get(1))
                .instructions("No onions")
                .quantity(2)
                .modifiers(Arrays.asList(modifiers1.get(0),modifiers1.get(1)))
                .build());

        shoppingCart.getItems().add(LineItem.builder()
                .product(productList.get(2))
                .quantity(1)
                .build());

        shoppingCart.getItems().add(LineItem.builder()
                .product(productList.get(3))
                .quantity(2)
                .build());

        checkout.setShoppingCart(shoppingCart);

        log.info("Customer updates the shopping cart");

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","CART")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200 );

        checkout.getShoppingCart().getItems().remove(3);

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","CART")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());

        checkout.getShippingInfo().setCustomerPosition(null);

        log.info("Customer updates Shipping Info with invalid data");

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","SHIPPING")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertNotEquals(result.getResponse().getStatus(), 200 );

        // Lets get the current Checkout on Session and verify that the customer position haven't been changed
        result = this.mvc.perform(get("/checkout")
                .session(httpSession))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());


        JsonNode jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        String responseJSON = jsonNode.get("response").toString();
        Checkout current1 = mapper.readValue(responseJSON, Checkout.class);

        assertNotNull(current1.getShippingInfo().getCustomerPosition());

        checkout = current1;

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .paymentMethod(PaymentInfo.PaymentMethod.CASH)
                .paymentSession("RandomPaymentSessionId")
                .build();

        checkout.setPaymentInfo(paymentInfo);

        log.info("Customer updates Payment Info");

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","PAYMENT")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200 );

        log.info("Customer removes some modifiers from one product");

        checkout.getShoppingCart().getItems().get(1).getModifiers().remove(0);

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","CART")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200 );

        log.info("Customer adds a product that belongs to a different store and finishes the checkout");

        result = this.mvc.perform(get("/checkout")
                .session(httpSession))
                .andReturn();

        jsonNode = mapper.readTree(result.getResponse().getContentAsString());
        responseJSON = jsonNode.get("response").toString();
        Checkout backup = mapper.readValue(responseJSON, Checkout.class);

        ShoppingCart shoppingCartBeforeAdding = backup.getShoppingCart();

        Collection c2 =
                collectionRepository.findAllByStoreVariantId(s2.getId()).get(0);
        List<Product> productList2 =
                productRepository.findAllByCollection_IdAndStatus(c2.getId(), ProductStatus.ACTIVE);

        LineItem lineItem = LineItem.builder().product(productList2.get(0)).quantity(5).build();
        checkout.getShoppingCart().getItems().add(lineItem);

        result = this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","CART")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        log.info("Response->"+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200 );

        /** Mock Principal (By default a customer account with bunny has been created on InitConfig)**/


        result = this.mvc.perform(post("/checkout/complete")
                .session(httpSession)
                .principal(principal))
                .andReturn();

        assertNotEquals(result.getResponse().getStatus(), 200);

        log.info(result.getResponse().getContentAsString());

        log.info("Customer Finishes the Checkout");

        checkout.setShoppingCart(shoppingCartBeforeAdding);

        this.mvc.perform(post("/checkout/update")
                .contentType("application/json")
                .session(httpSession)
                .param("attribute","CART")
                .content(mapper.writeValueAsString(checkout)))
                .andReturn();

        result = this.mvc.perform(post("/checkout/complete")
                .session(httpSession)
                .principal(principal))
                .andReturn();

        log.info("The Checkout created is: "+result.getResponse().getContentAsString());
        assertEquals(result.getResponse().getStatus(), 200);

    }

}
