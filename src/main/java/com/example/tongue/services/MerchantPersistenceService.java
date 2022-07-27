package com.example.tongue.services;

import com.example.tongue.domain.checkout.Position;
import com.example.tongue.domain.merchant.Merchant;
import com.example.tongue.domain.merchant.Store;
import com.example.tongue.domain.merchant.StoreVariant;
import com.example.tongue.repositories.merchant.MerchantRepository;
import com.example.tongue.repositories.merchant.StoreRepository;
import com.example.tongue.repositories.merchant.StoreVariantRepository;
import com.example.tongue.security.*;
import com.example.tongue.security.domain.MerchantAccount;
import com.example.tongue.security.domain.MerchantRegistrationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class MerchantPersistenceService {

    private MerchantRepository merchantRepository;
    private StoreRepository storeRepository;
    private StoreVariantRepository storeVariantRepository;
    private MerchantAccountRepository accountRepository;

    public MerchantPersistenceService(@Autowired MerchantAccountRepository accountRepository){
        this.accountRepository=accountRepository;
    }

    public void registerNewMerchantAccount(MerchantRegistrationForm registrationForm, String password){

        MerchantAccount account = new MerchantAccount(registrationForm.getEmail(), password);
        account = accountRepository.save(account);

        Merchant merchant = Merchant.builder()
                .account(account)
                .ownerName(registrationForm.getFirstName())
                .email(registrationForm.getEmail())
                .phoneNumber(registrationForm.getPhone())
                .build();
        merchant = merchantRepository.save(merchant);

        Store store = Store.builder()
                .merchant(merchant)
                .name(registrationForm.getStoreName())
                .owner(registrationForm.getFirstName())
                .createdAt(Instant.now())
                .build();
        store = storeRepository.save(store);

        StoreVariant storeVariant = StoreVariant.builder()
                .store(store)
                .name(registrationForm.getStoreName())
                .currency("US")
                .postalCode("EC")
                .hasActiveDiscounts(false)
                .allowCashPayments(true)
                .location(
                        Position.builder()
                                .address(registrationForm.getStoreAddress())
                                .latitude(0f)
                                .longitude(0f)
                                .owner(registrationForm.getStoreName())
                                .build()
                )
                .build();
        storeVariantRepository.save(storeVariant);

    }

}
