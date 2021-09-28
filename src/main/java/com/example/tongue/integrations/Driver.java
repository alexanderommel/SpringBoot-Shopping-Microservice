package com.example.tongue.integrations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Driver {
    @Id @GeneratedValue
    private Long id;
}
