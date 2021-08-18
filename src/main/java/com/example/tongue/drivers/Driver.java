package com.example.tongue.drivers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Driver {
    @Id @GeneratedValue
    private Long id;
}
