package com.example.tongue.core.configurations;

import com.example.tongue.checkout.filters.CheckoutCompletionFlow;
import com.example.tongue.checkout.filters.CheckoutSession;
import com.example.tongue.checkout.filters.CheckoutUpgradeFlow;
import com.example.tongue.checkout.filters.CheckoutValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckoutConfiguration {

    @Bean
    public CheckoutValidation checkoutValidation(){
        return new CheckoutValidation();
    }

    @Bean
    public CheckoutSession checkoutSession(){
        return new CheckoutSession();
    }

    @Bean
    public CheckoutCompletionFlow completionFlow(){
        return new CheckoutCompletionFlow();
    }

    @Bean
    public CheckoutUpgradeFlow upgradeFlow(){ return new CheckoutUpgradeFlow();}

}
