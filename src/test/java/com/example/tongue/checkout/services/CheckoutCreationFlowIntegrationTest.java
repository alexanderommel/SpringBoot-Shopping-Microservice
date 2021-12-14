package com.example.tongue.checkout.services;

import com.example.tongue.checkout.models.Checkout;
import com.example.tongue.checkout.models.FlowMessage;
import com.example.tongue.core.genericdata.Store1DataGenerator;
import com.example.tongue.core.domain.Position;
import com.example.tongue.merchants.models.*;
import com.example.tongue.merchants.repositories.*;
import com.example.tongue.shopping.models.Cart;
import com.example.tongue.shopping.models.LineItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CheckoutCreationFlowIntegrationTest {

    private CheckoutCreationFlow checkoutCreationFlow;
    //
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    StoreVariantRepository storeVariantRepository;
    @Autowired
    DiscountRepository discountRepository;
    @Autowired
    ProductImageRepository imageRepository;
    @Autowired
    GroupModifierRepository groupModifierRepository;
    @Autowired
    ModifierRepository modifierRepository;
    @Autowired
    CollectionRepository collectionRepository;
    @Autowired
    CollectionProductAllocationRepository
    collectionProductAllocationRepository;
    @Autowired
    HttpSession httpSession;
    @Autowired
    CheckoutValidation checkoutValidation;
    @Autowired
    CheckoutSession checkoutSession;

    //

    @BeforeAll
    public void setUp(){
        /** Generate data for testing **/
        Store1DataGenerator.getInstance( productRepository,
                 storeRepository,
                 merchantRepository,
                 storeVariantRepository,
                 discountRepository,
                 imageRepository,
                 groupModifierRepository,
                 modifierRepository,
                 collectionRepository,
                collectionProductAllocationRepository);
        /** **/
    }

    private void describeGeneratedData(){
        Long storeVariantId = storeVariantRepository.findAll().get(0).getId();
        System.out.println("Store Variant Id: "+storeVariantId);
        List<Collection> collections =
                collectionRepository.findAllByStoreVariantId(storeVariantId,null ).getContent();
        for (Collection c:collections
        ) {
            Long collectionId = c.getId();
            System.out.println("Collection id: "+collectionId);
            System.out.println("Collection title: "+c.getTitle());
            List<CollectionProductAllocation> allocations =
                    collectionProductAllocationRepository.findAllByCollection_Id(collectionId,null).getContent();
            List<Long> ids = new ArrayList<>();
            for (CollectionProductAllocation a:allocations
                 ) {
                Long productId = a.getProduct().getId();
                ids.add(productId);
            }
            System.out.println("Product ids: "+ids.toString());
        }
    }

    @Before
    public void before(){
        checkoutCreationFlow = new CheckoutCreationFlow(checkoutValidation,checkoutSession);
        System.out.println("HTTP SESSION ID: "+httpSession.getId());
    }

    @Test
    public void showDataInformation(){
        System.out.println("---Data generated info---");
        describeGeneratedData();
        System.out.println("\n");
    }

    @Test
    public void shouldReturnTrueGivenThatCheckoutEntryIsValid(){
        /** Input **/
        Checkout checkout = new Checkout();
        checkout = createBasicValidCheckout();
        /** **/
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        assertTrue(flowMessage.isSolved());
    }

    @Test
    public void givenCheckoutWithoutOriginWhenCreatingThenReturnValidationErrorStage(){
        String expected = "Validation error";
        Checkout checkout = createBasicValidCheckout();
        checkout.setOrigin(null);
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        assertEquals(flowMessage.getErrorStage(),expected);

    }

    @Test
    public void givenCheckoutWithNoExistingProductIdWhenCreatingThenReturnErrorMessage(){
        String expected = "No such Product with id '9994'";
        Checkout checkout = createBasicValidCheckout();
        checkout.getCart().getItems().get(0).getProduct().setId(9994L);
        FlowMessage flowMessage = checkoutCreationFlow.run(checkout,httpSession);
        assertEquals(expected,flowMessage.getErrorMessage());
    }

    private Checkout createBasicValidCheckout(){
        Checkout checkout = new Checkout();
        Cart cart = new Cart();
        cart.setInstructions("McLoving");
        LineItem item= new LineItem();
        item.setQuantity(2);
        Product product = new Product();
        product.setId(372L);
        Position destination = Position.builder().latitude(22.2F).longitude(44.0F).build();
        Position origin = Position.builder().latitude(10.0F).longitude(5.552222F).build();
        StoreVariant storeVariant = new StoreVariant();
        storeVariant.setId(3L);
        item.setProduct(product);
        cart.addItem(item);
        checkout.setCart(cart);
        checkout.setOrigin(origin);
        checkout.setDestination(destination);
        checkout.setStoreVariant(storeVariant);
        return checkout;
    }

}