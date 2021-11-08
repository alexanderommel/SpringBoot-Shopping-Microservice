package com.example.tongue.security;

import com.example.tongue.core.authentication.User;
import com.example.tongue.core.authentication.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class JwtAuthenticationManager implements AuthenticationManager {

    //Fields
    private UserRepository userRepository;
    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationManager.class);

    public JwtAuthenticationManager(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()){
            User user = optional.get();
            logger.info("User found");
            Authentication authentication1 =
                    new UsernamePasswordAuthenticationToken(user.getId(),
                            authentication.getCredentials(),
                            user.getRoles());
            return authentication1;
        }
        logger.info("User not found");
        return null;
    }
}
