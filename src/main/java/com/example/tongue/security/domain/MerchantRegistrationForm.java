package com.example.tongue.security.domain;

import lombok.Data;

@Data
public class MerchantRegistrationForm {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String storeName;
    private String storeAddress;
    private String storeIdentificationNumber;

}
