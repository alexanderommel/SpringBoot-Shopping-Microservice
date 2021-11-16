package com.example.tongue.customers.webcontrollers;

import com.example.tongue.core.authentication.UserRepository;
import com.example.tongue.customers.models.Customer;
import com.example.tongue.customers.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CustomerRestController {

    private CustomerRepository customerRepository;
    private static final Logger logger =
            LoggerFactory.getLogger(CustomerRestController.class);

    public CustomerRestController(@Autowired CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }

    @GetMapping("customers/register")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/customers/login")
    public ResponseEntity<Map<String,Object>> login(Principal principal){
        Map<String,Object> response = new HashMap<>();
        String userId = principal.getName();
        logger.info("Retrieving Customer Entity assigned to user '"+userId+"'");
        Optional<Customer> customerOptional = customerRepository.findByUserId(userId);
        if (customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            customer.setUser(null);
            response.put("response",customer);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
