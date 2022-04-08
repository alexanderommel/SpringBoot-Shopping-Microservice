package com.example.tongue.security;

import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class CustomerAuthenticationManager implements AuthenticationManager {

    private CustomerReplicationRepository customerRepository;

    public CustomerAuthenticationManager(@Autowired CustomerReplicationRepository customerRepository){
        this.customerRepository=customerRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authenticating user: "+authentication.getName());
        String username = authentication.getName();
        Optional<Customer> optional = customerRepository.findByUsername(username);
        if (optional.isEmpty())
            throw new UsernameNotFoundException("No such customer with username "+username);
        Authentication authentication1 =
                new UsernamePasswordAuthenticationToken(username,null, authentication.getAuthorities());
        return authentication1;
    }
}
