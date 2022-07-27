package com.example.tongue.security.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account implements TongueAccount {
    @Id
    protected String email;

    @Override
    public String getUsername() {
        return email;
    }
}
