package com.example.tongue.integration.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporalAccessToken{

    private int expirationHour;
    private int expirationMinute;
    private int expirationSecond;
    private String base64Encoding;

}