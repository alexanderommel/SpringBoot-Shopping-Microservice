package com.example.tongue.core.configurations;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.core.utilities.DataGenerator;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.CollectionStatus;
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

        StoreVariant s1 = StoreVariant.builder()
                .name("Sushi Store 6 De Diciembre")
                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
                .location(Position.builder()
                        .latitude(-0.59222f)
                        .longitude(-0.61922f)
                        .address("6 de diciembre y calle Quito")
                        .build())
                .storeFoodType(StoreVariantType.SUSHI)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Andrea Danhez")
                .representativePhone("5930955******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        StoreVariant s2 = StoreVariant.builder()
                .name("Hamburguesas De Colores")
                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
                .location(Position.builder()
                        .latitude(-0.51222f)
                        .longitude(-0.71922f)
                        .address("6 de diciembre y calle Quito")
                        .build())
                .storeFoodType(StoreVariantType.HAMBURGER)
                .allowCashPayments(true)
                .postalCode("EC")
                .representative("Daniel Star")
                .representativePhone("5930931******")
                .hasActiveDiscounts(false)
                .currency("USD")
                .store(store)
                .build();

        StoreVariant s3 = StoreVariant.builder()
                .name("Los Pollos Hermanos")
                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
                .location(Position.builder()
                        .latitude(-0.71282f)
                        .longitude(-0.71922f)
                        .address("6 de diciembre y calle Quito")
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


        List<StoreVariant> storeVariants = List.of(s1,s2,s3);

        for (StoreVariant s:storeVariants) {
            s = storeVariantRepository.save(s);
            log.info("Creating Store Variant with id->"+s.getId());
            List<Collection> collections =
                    dataGenerator.generateRandomizedCollections(5,s,"collection");
            for (Collection c:
                 collections) {
                c = collectionRepository.save(c);
                List<Product> products =
                        dataGenerator.generateRandomizedProducts(6,s,c,"product");
                for (Product p:
                     products) {
                    p = productPersistenceService.create(p);
                    List<GroupModifier> groupModifiers =
                            dataGenerator.generateGroupModifiers(1,3,s,p,"group");
                    for (GroupModifier g:
                         groupModifiers) {
                        g = groupModifierRepository.save(g);
                        List<Modifier> modifiers = dataGenerator.generateRandomizedModifiers(4,g,"modifier");
                        for (Modifier m:
                             modifiers) {
                            m = modifierPersistenceService.createModifier(m);
                        }
                    }
                }
            }
        }

    }

    private void generateStoreVariant1(Store store){

        StoreVariant storeVariant = StoreVariant.builder()
                .name("Los Pollos Hermanos")
                .storeImageURL("https://as01.epimg.net/epik/imagenes/2020/06/08/portada/1591616374_194956_1591616478_noticia_normal.jpg")
                .location(Position.builder()
                        .latitude(-0.71282f)
                        .longitude(-0.71922f)
                        .address("6 de diciembre y calle Quito")
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
                .title("Hamburguesas")
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
                .title("Hamburguesas")
                .build();

        Collection collection4 = Collection.builder()
                .storeVariant(storeVariant)
                .status(CollectionStatus.ACTIVE)
                .imageUrl("<no-image>")
                .title("Hamburguesas")
                .build();

        collection1 = collectionRepository.save(collection1);
        collection2 = collectionRepository.save(collection2);
        collection3 = collectionRepository.save(collection3);
        collection4 = collectionRepository.save(collection4);

    }

}
