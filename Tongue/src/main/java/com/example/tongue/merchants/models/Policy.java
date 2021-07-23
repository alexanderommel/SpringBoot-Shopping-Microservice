package com.example.tongue.merchants.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Policy {

    @Id @GeneratedValue
    private Long id;

    private String refunds;

}
