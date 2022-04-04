package com.example.tongue.core.configurations;

import com.example.tongue.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckoutConfiguration {

    @Bean
    public CheckoutCompletionFlow completionFlow(){
        return new CheckoutCompletionFlow();
    }

    @Bean
    public CheckoutUpgradeFlow upgradeFlow(){ return new CheckoutUpgradeFlow();}

}
