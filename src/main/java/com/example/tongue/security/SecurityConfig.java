package com.example.tongue.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private String customerServiceKey;
    private String serviceName;
    private CustomerAuthenticationManager customerAuthenticationManager;

    public SecurityConfig(@Autowired CustomerAuthenticationManager authenticationManager,
                          @Value("${customer.management.service.key}") String customerServiceKey,
                          @Value("${shopping.service.name}") String serviceName){
        this.customerAuthenticationManager=authenticationManager;
        this.customerServiceKey=customerServiceKey;
        this.serviceName=serviceName;
    }

    public CustomerJWTAuthenticationFilter customerJwtFilter(){
        CustomerJWTAuthenticationFilter filter = new CustomerJWTAuthenticationFilter(
                customerAuthenticationManager,
                customerServiceKey,
                serviceName);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/stores/**").permitAll()
                .antMatchers("/collections/**").permitAll()
                .antMatchers("/products/**").permitAll()
                .antMatchers("/group_modifiers/**").permitAll()
                .antMatchers("/dev/customers/**").permitAll()
                .antMatchers("/app/**").permitAll()
                .antMatchers("/auth/merchants/registe**").permitAll()
                .antMatchers("/auth/merchants/register/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(customerJwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement();
    }
}
