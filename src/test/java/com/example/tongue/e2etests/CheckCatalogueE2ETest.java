package com.example.tongue.e2etests;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.Collection;
import com.example.tongue.domain.merchant.enumerations.GroupModifierType;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.integration.shipping.ShippingBrokerResponse;
import com.example.tongue.integration.shipping.ShippingServiceBroker;
import com.example.tongue.integration.shipping.ShippingSummary;
import com.example.tongue.repositories.merchant.CollectionRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.resources.merchant.StoreVariantRestController;
import com.example.tongue.services.ProductModifierPersistenceService;
import com.example.tongue.services.ProductPersistenceService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
@AutoConfigureMockMvc
public class CheckCatalogueE2ETest {

    /**
     *
     * Description: This test emulates the situation where a customer open the android app,
     * until it picks a product and views the list of product modifiers.
     *
     * Expected result: The test should return a list of modifiers that belong to that product.
     *
     * Requirements: A product and its modifiers are created on runtime.
     *
     * Steps:
     * 1. Get the list of restaurants
     * 2. Pick one restaurant
     * 3. Get the list of collections (Menu)
     * 4. Pick one collection
     * 5. Get the list of products
     * 6. Pick one product
     * 7. Get the list of group modifiers
     * 8. Compare the modifiers with the modifiers persisted on runtime
     *
     * **/

    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    StoreVariantRestController storeVariantRestController;
    @Autowired
    ProductPersistenceService persistenceService;
    @Autowired
    ProductModifierPersistenceService modifierPersistenceService;
    @Autowired
    StoreVariantRepository storeVariantRepository;
    @Autowired
    CollectionRepository collectionRepository;
    // Mocked to testing on isolation
    @MockBean
    ShippingServiceBroker shippingServiceBroker;
    // ConnectionFactory and Rabbit are Mocked since we don't care about the connections with RabbitMQ
    @MockBean
    ConnectionFactory connectionFactory;
    @MockBean
    RabbitTemplate rabbitTemplate;
    @Autowired
    private MockMvc mvc;


    @Before
    public void setUp(){

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        ShippingSummary shippingSummary =  ShippingSummary.builder()
                .fee(BigDecimal.valueOf(1.90))
                .arrivalTime(LocalTime.now())
                .distance(new Distance(110, Metrics.KILOMETERS))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("summary",shippingSummary);

        ShippingBrokerResponse brokerResponse = ShippingBrokerResponse.builder()
                .isSolved(true)
                .messages(response)
                .build();

        Mockito
                .when(shippingServiceBroker.requestShippingSummary(ArgumentMatchers.any(),ArgumentMatchers.any()))
                .thenReturn(brokerResponse);
    }

    @Test
    public void checkCatalogueTest() throws Exception {

        /** Get the list of restaurants **/

        Position customerPosition = Position.builder()
                .owner("Alexander")
                .address("Quito av 123")
                .latitude(1.111042F)
                .longitude(1.122049F)
                .build();

        MvcResult result = this.mvc.perform(get("/stores")
                .contentType("application/json")
                .content(mapper.writeValueAsString(customerPosition)))
                .andReturn();

        String content = result.getResponse().getContentAsString();

        Map<?,?> map = mapper.readValue(content,Map.class);

        List<LinkedHashMap> storesHashMaps = (List<LinkedHashMap>) map.get("stores");
        LinkedHashMap store1 = storesHashMaps.get(0);
        Integer storeId = (Integer) store1.get("id");

        StoreVariant storeVariant = storeVariantRepository.findById(Long.valueOf(storeId)).get();

        log.info("(Test) store variant id ->"+String.valueOf(storeVariant.getId()));

        /** Get the list of collections (menu) for that store by calling endpoint /stores/{id}/menu **/

        result = this.mvc.perform(get("/stores/{id}/menu",storeId)).andReturn();

        map = mapper.readValue(result.getResponse().getContentAsString(),Map.class);

        List<LinkedHashMap> collectionsHashMaps = (List<LinkedHashMap>) map.get("response");
        LinkedHashMap collection = collectionsHashMaps.get(0);
        Integer collectionId = (Integer) collection.get("id");

        Collection collection1 = collectionRepository.findById(Long.valueOf(collectionId)).get();

        log.info("(Test) Collection id->"+String.valueOf(collection1.getId()));

        /** Persist a Product with its modifiers **/

        Map<String, Object> expected = addProductAndModifiers(storeVariant,collection1);
        Product p = (Product) expected.get("PRODUCT");
        GroupModifier groupModifier = (GroupModifier) expected.get("GROUP_MODIFIER");
        List<Modifier> modifiers = (List<Modifier>) expected.get("MODIFIERS");

        /** Get the list of products for that collection **/

        result = this.mvc.perform(get("/collections/{id}/products",collection1.getId())).andReturn();
        map = mapper.readValue(result.getResponse().getContentAsString(),Map.class);
        List<LinkedHashMap> productHashMaps = (List<LinkedHashMap>) map.get("response");

        Boolean expectedProductId=false;

        log.info("Searching product id with id->"+p.getId());

        for (LinkedHashMap l:productHashMaps) {
            Integer productId = (Integer) l.get("id");
            log.info(String.valueOf(productId));
            if (String.valueOf(productId).equalsIgnoreCase(String.valueOf(p.getId())))
                expectedProductId=true;
        }

        assert expectedProductId;

        /** Get the list of modifiers for that product**/

        result = this.mvc.perform(get("/products/{id}/group_modifiers",p.getId())).andReturn();
        map = mapper.readValue(result.getResponse().getContentAsString(),Map.class);
        List<LinkedHashMap> groupHashMaps = (List<LinkedHashMap>) map.get("response");
        Boolean expectedGroupModifier = false;
        log.info("Searching group modifier id with id->"+groupModifier.getId());
        for (LinkedHashMap l:groupHashMaps
             ) {
            Integer groupId = (Integer) l.get("id");
            log.info(String.valueOf(groupId));
            if (String.valueOf(groupId).equalsIgnoreCase(String.valueOf(groupModifier.getId())))
                expectedGroupModifier=true;
        }

        assert expectedGroupModifier;

    }

    private Map<String,Object> addProductAndModifiers(StoreVariant storeVariant, Collection collection) throws Exception{

        Map<String,Object> map = new HashMap<>();

        Product product = Product.builder()
                .title("Cat-Burger")
                .originalPrice(BigDecimal.valueOf(6.50))
                .price(BigDecimal.valueOf(5.50))
                .currency_code("USD")
                .description("Nice cat burger")
                .handle("cat")
                .status(ProductStatus.ACTIVE)
                .collection(collection)
                .storeVariant(storeVariant)
                .build();

        product = persistenceService.create(product);

        GroupModifier g1 = GroupModifier.builder()
                .product(product)
                .storeVariant(storeVariant)
                .context("tipo de salsa")
                .type(GroupModifierType.OPTIONAL)
                .build();

        g1 = modifierPersistenceService.createGroupModifier(g1);

        Modifier m1 = Modifier.builder()
                .name("salsa azul")
                .price(BigDecimal.valueOf(0.50))
                .groupModifier(g1)
                .build();

        Modifier m2 = Modifier.builder()
                .name("salsa verde")
                .price(BigDecimal.valueOf(0.20))
                .groupModifier(g1)
                .build();

        m1 = modifierPersistenceService.createModifier(m1);
        m2 = modifierPersistenceService.createModifier(m2);

        List<Modifier> modifiers = Arrays.asList(m1,m2);

        map.put("MODIFIERS",modifiers);
        map.put("GROUP_MODIFIER",g1);
        map.put("PRODUCT",product);
        return map;
    }
}
