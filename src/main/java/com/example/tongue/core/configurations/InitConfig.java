package com.example.tongue.core.configurations;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.core.utilities.DataGenerator;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.CollectionStatus;
import com.example.tongue.domain.merchant.enumerations.ProductStatus;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.repositories.merchant.*;
import com.example.tongue.security.domain.MerchantAccount;
import com.example.tongue.security.MerchantAccountRepository;
import com.example.tongue.services.ProductModifierPersistenceService;
import com.example.tongue.services.ProductPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class InitConfig {

    private ProductRepository productRepository;
    private ProductImageRepository productImageRepository;
    private DiscountRepository discountRepository;
    private GroupModifierRepository groupModifierRepository;
    private ModifierRepository modifierRepository;
    private CollectionRepository collectionRepository;
    private MerchantRepository merchantRepository;
    private StoreRepository storeRepository;
    private StoreVariantRepository storeVariantRepository;
    private ProductPersistenceService productPersistenceService;
    private ProductModifierPersistenceService modifierPersistenceService;
    private CustomerReplicationRepository customerRepository;
    private DataGenerator dataGenerator;
    private MerchantAccountRepository merchantAccountRepository;

    public InitConfig(@Autowired ProductRepository productRepository,
                      @Autowired ProductImageRepository productImageRepository,
                      @Autowired DiscountRepository discountRepository,
                      @Autowired GroupModifierRepository groupModifierRepository,
                      @Autowired ModifierRepository modifierRepository,
                      @Autowired CollectionRepository collectionRepository,
                      @Autowired MerchantRepository merchantRepository,
                      @Autowired MerchantAccountRepository merchantAccountRepository,
                      @Autowired StoreRepository storeRepository,
                      @Autowired StoreVariantRepository storeVariantRepository,
                      @Autowired ProductPersistenceService productPersistenceService,
                      @Autowired ProductModifierPersistenceService modifierPersistenceService,
                      @Autowired CustomerReplicationRepository customerRepository,
                      @Autowired DataGenerator dataGenerator){

        this.customerRepository=customerRepository;
        this.productRepository=productRepository;
        this.productImageRepository=productImageRepository;
        this.discountRepository=discountRepository;
        this.groupModifierRepository=groupModifierRepository;
        this.modifierRepository=modifierRepository;
        this.collectionRepository=collectionRepository;
        this.merchantRepository=merchantRepository;
        this.storeRepository=storeRepository;
        this.storeVariantRepository=storeVariantRepository;
        this.productPersistenceService=productPersistenceService;
        this.modifierPersistenceService=modifierPersistenceService;
        this.dataGenerator=dataGenerator;
        this.merchantAccountRepository = merchantAccountRepository;

    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }


    @Bean
    public void initCustomerAccounts(){
        log.info("Creating Customer Testing Accounts 'bunny' and 'dummy'");
        Customer c1 = Customer.builder().username("bunny").build();
        Customer c2 = Customer.builder().username("dummy").build();
        Customer c3 = Customer.builder().username("funny").build();
        customerRepository.save(c1);
        customerRepository.save(c2);
        customerRepository.save(c3);
    }

    @Bean
    public void initDefaultData() throws Exception {

        log.info("Loading default data...");
        log.info("Data Generator Autowired fields empty? ->"
                + String.valueOf(dataGenerator.storeVariantRepository==null));

        MerchantAccount account = new MerchantAccount("alexanderommelsw@gmail.com","12345678");

        account = merchantAccountRepository.save(account);

        Merchant merchant = Merchant.builder()
                .ownerName("Alexander Rommel")
                .phoneNumber("5930959******")
                .email("alexander.rommel9988@gmail.com")
                .account(account)
                .build();

        merchant = merchantRepository.save(merchant);

        Store store = Store.builder()
                .domain("sushi.alexander.com")
                .contactEmail("valeria9988@gmail.com")
                .contactPhone("5930948******")
                .owner("Valeria")
                .merchant(merchant)
                .name("Sushi Store")
                .build();

        store = storeRepository.save(store);

//        StoreVariant s1 = StoreVariant.builder()
//                .name("Sushi Store 6 De Diciembre")
//                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
//                .location(Position.builder()
//                        .latitude(-0.59222f)
//                        .longitude(-0.61922f)
//                        .address("6 de diciembre y calle Quito")
//                        .build())
//                .storeFoodType(StoreVariantType.SUSHI)
//                .allowCashPayments(true)
//                .postalCode("EC")
//                .representative("Andrea Danhez")
//                .representativePhone("5930955******")
//                .hasActiveDiscounts(false)
//                .currency("USD")
//                .store(store)
//                .build();
//
//        StoreVariant s2 = StoreVariant.builder()
//                .name("Hamburguesas De Colores")
//                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
//                .location(Position.builder()
//                        .latitude(-0.51222f)
//                        .longitude(-0.71922f)
//                        .address("6 de diciembre y calle Quito")
//                        .build())
//                .storeFoodType(StoreVariantType.HAMBURGER)
//                .allowCashPayments(true)
//                .postalCode("EC")
//                .representative("Daniel Star")
//                .representativePhone("5930931******")
//                .hasActiveDiscounts(false)
//                .currency("USD")
//                .store(store)
//                .build();
//
//        StoreVariant s3 = StoreVariant.builder()
//                .name("Los Pollos Hermanos")
//                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
//                .location(Position.builder()
//                        .latitude(-0.71282f)
//                        .longitude(-0.71922f)
//                        .address("6 de diciembre y calle Quito")
//                        .build())
//                .storeFoodType(StoreVariantType.CHICKEN)
//                .allowCashPayments(true)
//                .postalCode("EC")
//                .representative("Gustavo Fring")
//                .representativePhone("5930911******")
//                .hasActiveDiscounts(false)
//                .currency("USD")
//                .store(store)
//                .build();
//
//
//        List<StoreVariant> storeVariants = List.of(s1,s2,s3);
//
//        for (StoreVariant s:storeVariants) {
//            s = storeVariantRepository.save(s);
//            log.info("Creating Store Variant with id->"+s.getId());
//            List<Collection> collections =
//                    dataGenerator.generateRandomizedCollections(5,s,"collection");
//            for (Collection c:
//                 collections) {
//                c = collectionRepository.save(c);
//                List<Product> products =
//                        dataGenerator.generateRandomizedProducts(6,s,c,"product");
//                for (Product p:
//                     products) {
//                    p = productPersistenceService.create(p);
//                    List<GroupModifier> groupModifiers =
//                            dataGenerator.generateGroupModifiers(1,3,s,p,"group");
//                    for (GroupModifier g:
//                         groupModifiers) {
//                        g = groupModifierRepository.save(g);
//                        List<Modifier> modifiers = dataGenerator.generateRandomizedModifiers(4,g,"modifier");
//                        for (Modifier m:
//                             modifiers) {
//                            m = modifierPersistenceService.createModifier(m);
//                        }
//                    }
//                }
//            }
//        }

        generateStoreVariant1(store);
        generateStoreVariant2(store);
        generateStoreVariant3(store);
        generateStoreVariant4(store);
        generateStoreVariant5(store);
        generateStoreVariant7(store);
        generateStoreVariant6(store);

    }

    private void generateStoreVariant1(Store store) throws Exception {

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Comida variada Delicatel")
                .storeImageURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdTVQbWjsdQxjhgsEHxrARbkuL-FmS9YVaNA&usqp=CAU")
                .location(Position.builder()
                        .latitude(-0.208793f)
                        .longitude(-78.496079f)
                        .address("El Ejido")
                        .build())

                .storeFoodType(StoreVariantType.PIZZA)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection1 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        Collection collection2 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Comida costeña")
                .build();

        Collection collection3 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pizzas")
                .build();

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Helados")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection2 = collectionRepository.save(collection2);
        collection3 = collectionRepository.save(collection3);
        collection4 = collectionRepository.save(collection4);

        List<Product> productsCollection1 = createHamburgerProducts(storeVariant,collection1);
        List<Product> productsCollection2 = createFoodProducts(storeVariant,collection2);
        List<Product> productsCollection3 = createPizzaProducts(storeVariant,collection3);
        List<Product> productsCollection4 = createIceCreamProducts(storeVariant,collection4);

        productPersistenceService.create(productsCollection1.get(0));
        productPersistenceService.create(productsCollection1.get(1));
        productPersistenceService.create(productsCollection1.get(9));
        productPersistenceService.create(productsCollection1.get(3));
        productPersistenceService.create(productsCollection1.get(7));

        productPersistenceService.create(productsCollection2.get(3));
        productPersistenceService.create(productsCollection2.get(5));
        productPersistenceService.create(productsCollection2.get(7));
        productPersistenceService.create(productsCollection2.get(8));

        productPersistenceService.create(productsCollection3.get(1));
        productPersistenceService.create(productsCollection3.get(3));
        productPersistenceService.create(productsCollection3.get(8));
        productPersistenceService.create(productsCollection3.get(9));
        productPersistenceService.create(productsCollection3.get(2));
        productPersistenceService.create(productsCollection3.get(4));

        productPersistenceService.create(productsCollection4.get(6));
        productPersistenceService.create(productsCollection4.get(8));
        productPersistenceService.create(productsCollection4.get(4));
        productPersistenceService.create(productsCollection4.get(3));
        productPersistenceService.create(productsCollection4.get(2));

    }

    private void generateStoreVariant7(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Con Helados")
                .storeImageURL("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/portada-platos-helados-1503509728.jpg?crop=1.00xw:0.802xh;0,0.0992xh&resize=480:*")
                .location(Position.builder()
                        .latitude(-0.215850f)
                        .longitude(-78.504204f)
                        .address("Av. 10 de Agosto")
                        .build())

                .storeFoodType(StoreVariantType.PIZZA)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Helados")
                .build();

        collection4 = collectionRepository.save(collection4);

        List<Product> productsCollection4 = createIceCreamProducts(storeVariant,collection4);

        productRepository.save(productsCollection4.get(0));
        productRepository.save(productsCollection4.get(1));
        productRepository.save(productsCollection4.get(2));
        productRepository.save(productsCollection4.get(3));
        productRepository.save(productsCollection4.get(4));
        productRepository.save(productsCollection4.get(5));
        productRepository.save(productsCollection4.get(6));
        productRepository.save(productsCollection4.get(7));
        productRepository.save(productsCollection4.get(8));
        productRepository.save(productsCollection4.get(9));

    }

    private void generateStoreVariant2(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Fast Chicken")
                .storeImageURL("https://ahumados.shop/wp-content/uploads/2021/07/Screenshot_58.png")
                .location(Position.builder()
                        .latitude(-0.200627f)
                        .longitude(-78.501157f)
                        .address("Av. América 'Universidad Central'")
                        .build())

                .storeFoodType(StoreVariantType.CHICKEN)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection1 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pollo frito")
                .build();

        Collection collection2 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        Collection collection3 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pizzas")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection2 = collectionRepository.save(collection2);
        collection3 = collectionRepository.save(collection3);

        List<Product> productsCollection1 = createChickenProducts(storeVariant,collection1);
        List<Product> productsCollection2 = createHamburgerProducts(storeVariant,collection2);
        List<Product> productsCollection3 = createPizzaProducts(storeVariant,collection3);

        productRepository.save(productsCollection1.get(0));
        productRepository.save(productsCollection1.get(1));
        productRepository.save(productsCollection1.get(9));
        productRepository.save(productsCollection1.get(3));
        productRepository.save(productsCollection1.get(7));

        productRepository.save(productsCollection2.get(3));
        productRepository.save(productsCollection2.get(1));
        productRepository.save(productsCollection2.get(7));
        productRepository.save(productsCollection2.get(8));

        productRepository.save(productsCollection3.get(5));
        productRepository.save(productsCollection3.get(6));
        productRepository.save(productsCollection3.get(8));
        productRepository.save(productsCollection3.get(3));
        productRepository.save(productsCollection3.get(2));
        productRepository.save(productsCollection3.get(1));

    }

    private void generateStoreVariant3(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Hamburguesas La Cruz")
                .storeImageURL("https://e00-marca.uecdn.es/assets/multimedia/imagenes/2019/02/26/15511968658125.jpg")
                .location(Position.builder()
                        .latitude(-0.208714f)
                        .longitude(-78.485770f)
                        .address("Toledo 'La Floresta'")
                        .build())

                .storeFoodType(StoreVariantType.PIZZA)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection1 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        Collection collection3 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pizzas")
                .build();

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Helados")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection3 = collectionRepository.save(collection3);
        collection4 = collectionRepository.save(collection4);

        List<Product> productsCollection1 = createHamburgerProducts(storeVariant,collection1);
        List<Product> productsCollection3 = createPizzaProducts(storeVariant,collection3);
        List<Product> productsCollection4 = createIceCreamProducts(storeVariant,collection4);

        productRepository.save(productsCollection1.get(2));
        productRepository.save(productsCollection1.get(5));
        productRepository.save(productsCollection1.get(6));
        productRepository.save(productsCollection1.get(3));
        productRepository.save(productsCollection1.get(8));

        productRepository.save(productsCollection3.get(5));
        productRepository.save(productsCollection3.get(6));
        productRepository.save(productsCollection3.get(7));
        productRepository.save(productsCollection3.get(8));
        productRepository.save(productsCollection3.get(9));

        productRepository.save(productsCollection4.get(9));
        productRepository.save(productsCollection4.get(1));
        productRepository.save(productsCollection4.get(5));
        productRepository.save(productsCollection4.get(8));

    }

    private void generateStoreVariant4(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Rincón Manabí")
                .storeImageURL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfUGhd0oQa4Y8h5JUsJNYY_WdVpiVUaZgrnw&usqp=CAU")
                .location(Position.builder()
                        .latitude(-0.219577f)
                        .longitude(-78.513233f)
                        .address("Palacio de Carondelet")
                        .build())

                .storeFoodType(StoreVariantType.WINGS)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection2 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Comida Manaba")
                .build();

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Helados")
                .build();

        collection2 = collectionRepository.save(collection2);
        collection4 = collectionRepository.save(collection4);

        List<Product> productsCollection2 = createFoodProducts(storeVariant,collection2);
        List<Product> productsCollection4 = createIceCreamProducts(storeVariant,collection4);


        productRepository.save(productsCollection2.get(0));
        productRepository.save(productsCollection2.get(5));
        productRepository.save(productsCollection2.get(1));
        productRepository.save(productsCollection2.get(2));
        productRepository.save(productsCollection2.get(3));
        productRepository.save(productsCollection2.get(4));
        productRepository.save(productsCollection2.get(6));
        productRepository.save(productsCollection2.get(7));
        productRepository.save(productsCollection2.get(8));
        productRepository.save(productsCollection2.get(9));


        productRepository.save(productsCollection4.get(6));
        productRepository.save(productsCollection4.get(8));
        productRepository.save(productsCollection4.get(4));
        productRepository.save(productsCollection4.get(3));
        productRepository.save(productsCollection4.get(2));

    }


    private void generateStoreVariant6(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Hamburguesas de la 10")
                .storeImageURL("data:image/jpeg;base64,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")
                .location(Position.builder()
                        .latitude(-0.217684f)
                        .longitude(-78.505520f)
                        .address("Av. 10 de Agosto")
                        .build())

                .storeFoodType(StoreVariantType.HAMBURGER)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection1 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        Collection collection3 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pizzas")
                .build();

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Helados")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection3 = collectionRepository.save(collection3);
        collection4 = collectionRepository.save(collection4);

        List<Product> productsCollection1 = createHamburgerProducts(storeVariant,collection1);
        List<Product> productsCollection3 = createPizzaProducts(storeVariant,collection3);
        List<Product> productsCollection4 = createIceCreamProducts(storeVariant,collection4);

        productRepository.save(productsCollection1.get(2));
        productRepository.save(productsCollection1.get(5));
        productRepository.save(productsCollection1.get(6));
        productRepository.save(productsCollection1.get(3));
        productRepository.save(productsCollection1.get(8));

        productRepository.save(productsCollection3.get(5));
        productRepository.save(productsCollection3.get(6));
        productRepository.save(productsCollection3.get(7));
        productRepository.save(productsCollection3.get(8));
        productRepository.save(productsCollection3.get(9));

        productRepository.save(productsCollection4.get(9));
        productRepository.save(productsCollection4.get(1));
        productRepository.save(productsCollection4.get(5));
        productRepository.save(productsCollection4.get(8));

    }

    private List<Product> createHamburgerProducts(StoreVariant storeVariant, Collection collection){

        Product h1 = Product.builder()
                .title("Doble Queso")
                .description("Disfruta de este deliciosa hamburguesa que con su doble queso te hará chupar los dedos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(5.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-455/adobe-burger-1-a.jpg")
                .build();

        Product h2 = Product.builder()
                .title("1/4 de libra")
                .description("Con esta hamburguesa disfrutaras de 1/4 de carne horneada al carbón.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(4.00))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-454/dia-hamburguesa-m.jpg")
                .build();
        Product h3 = Product.builder()
                .title("Hamburguesa tradicional")
                .description("	Los amigos de la tradición disfrutarán de lo lindo con esta hamburguesa clásica.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(7.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-457/clasica-burger-a.jpg")
                .build();
        Product h4 = Product.builder()
                .title("Hamburguesa de Quinoa y Zanahoria")
                .description("	Las alternativas vegetarianas y veganas de las hamburguesas han pasado de ser una rareza.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(2.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-454/dia-hamburguesa-m.jpg")
                .build();
        Product h5 = Product.builder()
                .title("Croissant Hamburguesa")
                .description("Si eres de esas personas que disfrutan como un niño probando nuevas combinaciones de sabores.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(4.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-458/croissant-hamburguesa-a.jpg")
                .build();
        Product h6 = Product.builder()
                .title("Hamburguesa con pan de centeno")
                .description("	Un buen pan hace que una hamburguesa estándar pueda convertirse en un bocado ‘gourmet’.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(5.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-456/burger-centeno-a.jpg")
                .build();
        Product h7 = Product.builder()
                .title("Hamburguesa de Salmón")
                .description("¡Qué versátil es el salmón en cocina! Aquí lo utilizaremos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(2.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-460/hamburguesas-salmon-a.jpg")
                .build();
        Product h8 = Product.builder()
                .title("Hamburguesa de Wagyu")
                .description("El wagyu es una raza de buey, originaria de la ciudad japonesa de Kobe.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(8.00))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-464/wagyu-burger-a.jpg")
                .build();
        Product h9 = Product.builder()
                .title("Mini Hamburguesitas")
                .description("Las hamburguesas en formato mini son una opción estupenda para un picoteo entre amigos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.00))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-461/mini-hamburguesas-a.jpg")
                .build();
        Product h10 = Product.builder()
                .title("Burguer de Steark TarTar")
                .description("Mostramos aquí cómo presentar una hamburguesa de forma distinta.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/noticiaslibros/20210528190401/dia-internacional-hamburguesa-recetas-2021/0-957-462/pan-negro-adobe-a.jpg")
                .build();

        ArrayList<Product> products = new ArrayList<>();
        products.add(h1);
        products.add(h2);
        products.add(h3);
        products.add(h4);
        products.add(h5);
        products.add(h6);
        products.add(h7);
        products.add(h8);
        products.add(h9);
        products.add(h10);

        return products;

    }

    private void generateStoreVariant5(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Pollos de la Valparaiso")
                .storeImageURL("https://www.cubaneandoconmario.com/wp-content/uploads/2020/12/145487-Buttermilk-Fried-Chicken-1-730x422.jpg")
                .location(Position.builder()
                        .latitude(-0.223753f)
                        .longitude(-78.504345f)
                        .address("Valparaiso 437")
                        .build())

                .storeFoodType(StoreVariantType.CHICKEN)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Gustavo Fring")
                .representativePhone("5930911******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        storeVariant = storeVariantRepository.save(storeVariant);

        /** Create some Collections **/

        Collection collection1 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pollo frito")
                .build();

        Collection collection2 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        Collection collection3 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Pizzas")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection2 = collectionRepository.save(collection2);
        collection3 = collectionRepository.save(collection3);

        List<Product> productsCollection1 = createChickenProducts(storeVariant,collection1);
        List<Product> productsCollection2 = createHamburgerProducts(storeVariant,collection2);
        List<Product> productsCollection3 = createPizzaProducts(storeVariant,collection3);

        productRepository.save(productsCollection1.get(2));
        productRepository.save(productsCollection1.get(4));
        productRepository.save(productsCollection1.get(5));
        productRepository.save(productsCollection1.get(1));
        productRepository.save(productsCollection1.get(6));

        productRepository.save(productsCollection2.get(7));
        productRepository.save(productsCollection2.get(4));
        productRepository.save(productsCollection2.get(6));
        productRepository.save(productsCollection2.get(1));

        productRepository.save(productsCollection3.get(7));
        productRepository.save(productsCollection3.get(4));
        productRepository.save(productsCollection3.get(8));
        productRepository.save(productsCollection3.get(3));
        productRepository.save(productsCollection3.get(2));
        productRepository.save(productsCollection3.get(9));

    }



    private List<Product> createFoodProducts(StoreVariant storeVariant,Collection collection){

        Product c1 = Product.builder()
                .title("Guiso de pescado")
                .description("Tradicional Guiso de la cultura ecuatoriana preparado con ingredientes nacionales.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(5.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200407165088/receta-guiso-pescado-pimiento-rojo/0-808-515/guiso-pescado-m.jpg")
                .build();
        Product c2 = Product.builder()
                .title("Sopa de pescado")
                .description("Es una sopa líquida, cuyo principal ingrediente es el pescado de ríos de la Amazonía.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(8.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("http://raquelenecuador.weebly.com/uploads/2/6/8/6/26861509/543451_orig.jpeg")
                .build();
        Product c3 = Product.builder()
                .title("Ceviche de Camarón")
                .description("Disfrute de un delicioso plato típico tradicional de la costa ecuatoriana.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(8.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://www.juiciocrudo.com/pics/nocrop/1280x960/538c844aa3483024e44e7622e7ac0c308133b614.jpg")
                .build();
        Product c4 = Product.builder()
                .title("Corvina con menestra")
                .description("Descubra y deléitese toda la sazón ecuatoriana con este típico plato.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(12.00))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://www.eluniverso.com/resizer/_sEwzpoA47_sUPkCLB28H3qFT5c=/887x670/smart/filters:quality(70)/cloudfront-us-east-1.images.arcpublishing.com/eluniverso/37ELL5G74VBNXGBM5YXMGNHYQY.jpg")
                .build();
        Product c5 = Product.builder()
                .title("Cangrejos")
                .description("Plato representativo de la costa ecuatoriana en el cual sobresale el delicioso cangrejo.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(15.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://cloudfront-us-east-1.images.arcpublishing.com/eluniverso/Y4VUXP3JP5APNJ7IG2CAHKAI4E.jpg")
                .build();
        Product c6 = Product.builder()
                .title("Seco de pollo")
                .description("Plato tradicional de la cocina ecuatoriana en el cual sobresale el pollo.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(4.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://beachlifeecuador.com/wp-content/uploads/2019/07/seco-de-pollo.jpg")
                .build();
        Product c7 = Product.builder()
                .title("Arroz marinero")
                .description("Un delicioso plato de la costa ecuatoriana en cual sobresale la mezcla de varios mariscos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.00))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://media-cdn.tripadvisor.com/media/photo-s/05/99/14/99/arroz-marinero.jpg")
                .build();
        Product c8 = Product.builder()
                .title("Tigrillo")
                .description("	Un exquisito plato en el cual el plátano verde es el protagonista.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://beachlifeecuador.com/wp-content/uploads/2019/07/tigrillo-de-verde.jpg")
                .build();
        Product c9 = Product.builder()
                .title("Ceviche mixto")
                .description("Delicioso plato ecuatoriano en el cual los mariscos sobresalen.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(7.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://quitotourbus.com/wp-content/uploads/2019/09/comidas-tipicas-del-ecuador_ceviche.jpg")
                .build();
        Product c10 = Product.builder()
                .title("Encebollado")
                .description("Caldo representativo de la cultura ecuatoriana.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(4.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("http://raquelenecuador.weebly.com/uploads/2/6/8/6/26861509/543451_orig.jpeg")
                .build();

        ArrayList<Product> products = new ArrayList<>();

        products.add(c1);
        products.add(c2);
        products.add(c3);
        products.add(c4);
        products.add(c5);
        products.add(c6);
        products.add(c7);
        products.add(c8);
        products.add(c9);
        products.add(c10);

        return products;

    }

    private List<Product> createPizzaProducts(StoreVariant storeVariant, Collection collection){

        Product p1 = Product.builder()
                .title("Pizza de pepperoni y mozzarella")
                .description("Aunque es uno de los ingredientes más famosos en la elaboración de pizzas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(12.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20220208204252/pizza-pepperoni-mozzarella/1-48-890/pepperoni-pizza-abob-m.jpg")
                .build();
        Product p2 = Product.builder()
                .title("Pizza rápida de calabacín, jamón y queso")
                .description("Ojo a este plato que está a medio caballo entre una pizza healthy.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20220714213687/pizza-rapida-de-calabacin-jamon-y-queso/1-115-435/pizza-rapida-de-calabacin-jamon-y-queso-m.jpg")
                .build();
        Product p3 = Product.builder()
                .title("Pizza con jamón, olivas, rúcula y parmesano")
                .description("Más allá de los eslóganes comerciales, en una pizza, sin lugar a dudas, el secreto está en la masa.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(13.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20220817215358/pizza-con-jamon-olivas-rucula-y-parmesano/1-126-204/pizza-con-jamon-olivas-rucola-y-parmesano-t.jpg")
                .build();
        Product p4 = Product.builder()
                .title("Pizza vegetariana")
                .description("El tomate suele ser uno de los imprescindibles de casi cualquier pizza.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(23.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20210121182995/pizza-vegetariana-berenjena/0-911-931/pizza-vegeta-age-m.jpg")
                .build();
        Product p5 = Product.builder()
                .title("Pizza de tomate, cebolla y piñones")
                .description("Esta pizza vegetariana cuenta con una combinación de sabores de lo más apetecible.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20210121182994/pizza-tomate-cebolla-pinones/0-911-928/pizza-pinones-age-m.jpg")
                .build();
        Product p6 = Product.builder()
                .title("Pizza de verduras, nueces y cebolla caramelizada")
                .description("Una pizza saludable, elaborada con verduras y nueces.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(18.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20210927196708/pizza-verduras-nueces-cebolla-caramelizada/0-998-920/pizza-bosquet-m.jpg")
                .build();
        Product p7 = Product.builder()
                .title("Pizzas de salchichas y tocino")
                .description("Para cuya elaboración se utilizaron ingredientes principales.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(9.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20220208204253/pizza-salchichas-bacon/1-48-891/pizza-salchichas-age-m.jpg")
                .build();
        Product p8 = Product.builder()
                .title("Pizza con pastrami")
                .description("El pastrami es una carne que se somete a un marinado en salmuera.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(16.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20210209184032/pizza-de-pastrami/0-918-6/pizza-adobe-m.jpg")
                .build();
        Product p9 = Product.builder()
                .title("Pizza de coliflor")
                .description("Habrás comido pizzas de todos los tipos y con masas más o menos finas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(9.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190611143690/pizza-de-coliflor/0-689-579/pizza-coliflor-m.jpg")
                .build();
        Product p10 = Product.builder()
                .title("Pizzas Halloween")
                .description("Apenas necesitarás diez minutos para crear estas divertidas mini pizzas para tu cena de Halloween.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(25.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20191023152377/mini-pizzas-halloween-momias/0-737-291/halloween-pizzas-cenas-rapidas-m.jpg")
                .build();

        ArrayList<Product> products = new ArrayList<>();

        products.add(p1);
        products.add(p2);
        products.add(p3);
        products.add(p4);
        products.add(p5);
        products.add(p6);
        products.add(p7);
        products.add(p8);
        products.add(p9);
        products.add(p10);

        return products;

    }

    private List<Product> createChickenProducts(StoreVariant storeVariant, Collection collection){

        Product k1 = Product.builder()
                .title("Pollo frito con jengibre y corteza de hierbas")
                .description("Si lo que estás buscando es un plato sabroso. Con cerveza, jengibre y orégano.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190208136898/pollo-frito-jenjibre-hierbas-cs/0-644-396/h_89165324-m.jpg")
                .build();
        Product k2 = Product.builder()
                .title("Alitas de pollo a la americana o buffalo wings")
                .description("Preparado en su propio adobo para una de las elaboraciones más populares, el pollo frito.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(7.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200205159814/alitas-de-pollo-a-la-americana-buffalo-wings/0-778-671/aitas-de-pollo-a-la-americana-o-buffalo-wings-m.jpg")
                .build();
        Product k3 = Product.builder()
                .title("Dedos de pollo")
                .description("Los 'dedos' son unos palitos de pollo sin piel ni huesos rebozados y fritos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(7.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190702145114/fingers-pollo/0-697-648/fingers-m.jpg")
                .build();
        Product k4 = Product.builder()
                .title("Alitas de pollo rebozadas con cornflakes y mostaza")
                .description("Las alitas de pollo son todo un clásico de la cocina norteamericana. ")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(5.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190702145110/alitas-pollo-cornflakes-mostaza/0-697-632/alitas-pollo-cornflakes-m.jpg")
                .build();
        Product k5 = Product.builder()
                .title("Bocaditos de alitas de pollo caramelizadas")
                .description("Las alitas son una de las partes del pollo más sabrosas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(8.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190702145108/bocaditos-alitas-pollo/0-697-629/bocaditos-alitas-caram-m.jpg")
                .build();
        Product k6 = Product.builder()
                .title("Alitas de pollo rebozadas con yogur y limón")
                .description("Son económicas, gustan tanto a niños como a mayores y... ¡están riquísimas!.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(6.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200309162621/alitas-pollo-rebozadas-salsa-yogur/0-795-569/alitas-m.jpg")
                .build();
        Product k7 = Product.builder()
                .title("Pechugas de pollo a la Villeroy")
                .description("La salsa 'Villeroy' se parece a la bechamel pero lleva queso rallado y clara de huevo.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(10.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200925176053/pechugas-pollo-villeroy/0-870-651/villaroy-adobe-m.jpg")
                .build();
        Product k8 = Product.builder()
                .title("Alitas de pollo en salsa de queso")
                .description("Son muy populares fritas, marinadas y acompañadas de diferentes salsas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(8.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/2017052294861/receta-alitas-pollo-salsa-queso/0-451-304/alitas-queso-m.jpg")
                .build();
        Product k9 = Product.builder()
                .title("Alitas pimentoneras")
                .description("El adobo o marinado también puede contribuir a hacer una receta deliciosa.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(12.50))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190702145113/alitas-pimentoneras/0-697-647/alitas-pimentoneras-m.jpg")
                .build();
        Product k10 = Product.builder()
                .title("'Panini' de pollo con mozarela y cebolla frita.")
                .description("Adobamos unos filetes pollo para meterlos entre 2 panes que tostamos junto a unas rodajas de mozarela")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(15.0))
                .originalPrice(BigDecimal.valueOf(3.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20201002176517/panini-pollo-mozarela-cebolla/0-873-533/pollo-mozarela-adobe-m.jpg")
                .build();

        ArrayList<Product> products = new ArrayList<>();

        products.add(k1);
        products.add(k2);
        products.add(k3);
        products.add(k4);
        products.add(k5);
        products.add(k6);
        products.add(k7);
        products.add(k8);
        products.add(k9);
        products.add(k10);

        return products;

    }

    private List<Product> createIceCreamProducts(StoreVariant storeVariant, Collection collection){

        Product i1 = Product.builder()
                .title("Helado de cheesecake")
                .description("Con la llegada del calor, los helados se ofrecen como una de las mejores alternativas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20191024152465/helado-cheesecake/0-737-772/cheesecake-m.jpg")
                .build();
        Product i2 = Product.builder()
                .title("Helado de zanahoria")
                .description("Helados se pueden hacer de infinidad de sabores.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(2.0))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20210811194439/helado-de-zanahoria/0-984-539/zanahoria-adobe-m.jpg")
                .build();
        Product i3 = Product.builder()
                .title("Helado de galletas")
                .description("Para este helado, se pueden emplear diferentes tipos de galletas.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(3.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20220613211684/helado-de-galletas/1-100-773/speculoos-adobe-m.jpg")
                .build();
        Product i4 = Product.builder()
                .title("Helado de chocolate")
                .description("El helado de chocolate es un clásico que nunca falla. ")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(2.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200212160473/helado-chocolate-facil/0-782-495/helado-chocolate-m.jpg")
                .build();
        Product i5 = Product.builder()
                .title("Helado de chirimoya")
                .description("La chirimoya, también conocida como anón o anona.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20191105153223/helado-de-chirimoya/0-741-731/helado-chirimoya-age-m.jpg")
                .build();
        Product i6 = Product.builder()
                .title("Helado de higos")
                .description("El higo es una fruta bastante dulce que nos aporta abundante energía, minerales y fibra.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20180627126335/helado-higos/0-580-308/helado-higos-m.jpg")
                .build();
        Product i7 = Product.builder()
                .title("Helado de melocotón")
                .description("Te presentamos una receta apta para vegetarianos.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200521168424/helado-melocoton-facil/0-826-248/helado-melocoton-m.jpg")
                .build();
        Product i8 = Product.builder()
                .title("Helado de salmorejo")
                .description("No hay dos salmorejos iguales, pues el plato permite elaborarlo al gusto de cada uno.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200603169311/receta-helado-salmorejo/0-831-386/helado-salmorejo-m.jpg")
                .build();
        Product i9 = Product.builder()
                .title("Helado de fresa")
                .description("A veces, lo más sencillo es lo que más nos gusta, como este helado de fresas maduras.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(1.50))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20200615170125/helado-fresa/0-835-864/helado-fresas-oberon-m.jpg")
                .build();
        Product i10 = Product.builder()
                .title("Helado de aguacate")
                .description("Este helado, además de tomarlo solo, como postre, es un complemento ideal de sopas frías.")
                .status(ProductStatus.ACTIVE)
                .price(BigDecimal.valueOf(2.0))
                .originalPrice(BigDecimal.valueOf(1.50))
                .currency_code("USD")
                .storeVariant(storeVariant)
                .collection(collection)
                .imageUrl("https://images.hola.com/imagenes/cocina/recetas/20190604143244/helado-de-aguacate/0-686-835/helado-de-aguacate-m.jpg")
                .build();

        ArrayList<Product> products = new ArrayList<>();

        products.add(i1);
        products.add(i2);
        products.add(i3);
        products.add(i4);
        products.add(i5);
        products.add(i6);
        products.add(i7);
        products.add(i8);
        products.add(i9);
        products.add(i10);

        return products;

    }

}
