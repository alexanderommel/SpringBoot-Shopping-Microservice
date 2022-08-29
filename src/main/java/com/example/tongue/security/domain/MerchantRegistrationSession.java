package com.example.tongue.security.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantRegistrationSession {

    private String sessionId;
    private String payload;

}
