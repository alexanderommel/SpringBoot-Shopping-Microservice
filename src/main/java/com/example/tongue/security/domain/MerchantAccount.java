package com.example.tongue.security.domain;


import com.example.tongue.security.domain.Account;

import javax.persistence.Entity;

@Entity
public class MerchantAccount  extends Account {
    private String password;

    public MerchantAccount(String email, String password){
        this.email = email;
        this.password = password;
    }

    public MerchantAccount() {}
    public String getPassword(){
        return password;
    }
}
