package com.example.tongue.resources.customer;

import com.example.tongue.integration.customers.Customer;
import com.example.tongue.integration.customers.CustomerReplicationRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CustomerRestController {

    private CustomerReplicationRepository customerRepository;
    private Environment environment;
    private String customerServiceKey;

    public CustomerRestController(@Autowired CustomerReplicationRepository customerRepository,
                                  @Autowired Environment environment,
                                  @Value("${customer.management.service.key}") String customerServiceKey){

        this.customerRepository=customerRepository;
        this.environment=environment;
        this.customerServiceKey=customerServiceKey;
    }

    @PostMapping("/dev/customers/oauth")
    public ResponseEntity<Map<String,Object>> login(@RequestBody Customer customer){
        log.info("OAuth Developer profile end point called");
        Map<String,Object> response = new HashMap<>();
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")){
            log.info("Profile accepted");
            String username = customer.getUsername();
            Optional<Customer> wrapper = customerRepository.findByUsername(username);
            if (wrapper.isEmpty()){
                log.info("No user exists with username '"+username+"'");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Customer customer1 = wrapper.get();
            String jwt = createValidJWTToken(customer1.getUsername());
            response.put("jwt",jwt);
            log.info("Jwt generated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        log.info("This endpoint is callable only if current profile is 'dev'");
        return new ResponseEntity<>(HttpStatus.GONE);
    }

    @PostMapping("/customers/authenticate")
    public ResponseEntity authenticate(Principal p){
        if (p==null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        log.info("User "+p.getName()+" authenticated!");
        return new ResponseEntity(HttpStatus.OK);
    }

    private String createValidJWTToken(String username) {

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("DRIVER");

        String token = Jwts
                .builder()
                .setId("pass")
                .setSubject(username)
                .setIssuer("customer-management-service")
                .setAudience("shopping-service")
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 Day
                .signWith(SignatureAlgorithm.HS512, customerServiceKey.getBytes()).compact();

        return "Bearer " + token;
    }
}
