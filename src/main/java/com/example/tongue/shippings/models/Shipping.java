package com.example.tongue.shippings.models;

import com.example.tongue.locations.models.Location;
import com.example.tongue.payments.models.Payment;
import com.example.tongue.payments.models.Transaction;

import javax.persistence.*;

@Entity
public class Shipping {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Payment payment;

    @OneToOne
    private Transaction transaction;

    @ManyToOne
    private Location destination;
}
