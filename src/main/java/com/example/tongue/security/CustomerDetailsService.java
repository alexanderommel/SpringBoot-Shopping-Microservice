package com.example.tongue.security;

import com.example.tongue.core.authentication.User;
import com.example.tongue.core.authentication.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {
    //attributes
    private UserRepository repository;

    public CustomerDetailsService(UserRepository repository){
        this.repository=repository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = repository.getUserByUsername(s);
        //SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        //ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //authorities.add(authority);
        if(user!=null){
            System.out.println("User exists");
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), user.getRoles()
            );
        }
        System.out.println("User doesn't exists");
        return null;
    }
}
