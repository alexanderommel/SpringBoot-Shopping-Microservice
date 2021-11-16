package com.example.tongue.security;

import com.example.tongue.core.authentication.UserRepository;
import com.example.tongue.security.implementations.CustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableJpaRepositories
public class CustomerConfigSecurity extends WebSecurityConfigurerAdapter {
    //attributes
    private UserRepository repository;
    public CustomerConfigSecurity(@Autowired UserRepository repository){
        this.repository=repository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(new CustomerDetailsService(this.repository));
        provider.setPasswordEncoder(passwordEncoder());
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/customers/**")
                .authorizeRequests().antMatchers("(customers/register")
                .permitAll();
                /*.authorizeRequests()
                .antMatchers("/customers/register").permitAll()
                .anyRequest().hasAnyAuthority("USER","ADMIN")
                //.anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .httpBasic()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

                 */
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}
