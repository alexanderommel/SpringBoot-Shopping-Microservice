package com.example.tongue.security.domain;

import com.example.tongue.security.domain.MerchantRegistrationSession;
import lombok.Data;

@Data
public class MerchantPasswordForm {

    private String password;
    private MerchantRegistrationSession session;

}
