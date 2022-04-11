package com.example.tongue.core.configurations;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.core.utilities.DataGenerator;
import com.example.tongue.domain.merchant.*;
import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.repositories.merchant.*;
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

    public InitConfig(@Autowired ProductRepository productRepository,
                      @Autowired ProductImageRepository productImageRepository,
                      @Autowired DiscountRepository discountRepository,
                      @Autowired GroupModifierRepository groupModifierRepository,
                      @Autowired ModifierRepository modifierRepository,
                      @Autowired CollectionRepository collectionRepository,
                      @Autowired MerchantRepository merchantRepository,
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

        Merchant merchant = Merchant.builder()
                .ownerName("Alexander Rommel")
                .phoneNumber("5930959******")
                .email("alexander.rommel9988@gmail.com")
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
                .storeImageURL("https://images.com/44")
                .location(Position.builder().latitude(-0.59222f).longitude(-0.61922f).build())
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
                .storeImageURL("https://images.com/45")
                .location(Position.builder().latitude(-0.51222f).longitude(-0.71922f).build())
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
                .storeImageURL("https://images.com/46")
                .location(Position.builder().latitude(-0.71282f).longitude(-0.71922f).build())
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

}
