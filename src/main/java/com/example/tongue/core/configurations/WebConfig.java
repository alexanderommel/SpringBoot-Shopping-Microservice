package com.example.tongue.core.configurations;

import com.example.tongue.core.converters.CheckoutAttributeConverter;
import com.example.tongue.sales.checkout.CheckoutAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //@Bean
    //public CheckoutAttributeConverter converter(){
      //  return new CheckoutAttributeConverter();
    //}

    //@Autowired
    //private CheckoutAttributeConverter converter;

    //@Bean
    //public LocalValidatorFactoryBean factoryBean(){
     //   return new LocalValidatorFactoryBean();
    //}
    @Override
    public void addFormatters(FormatterRegistry registry) {
        System.out.println("ADDING CONVERTER");
        registry.addConverter(new CheckoutAttributeConverter());
    }


}
