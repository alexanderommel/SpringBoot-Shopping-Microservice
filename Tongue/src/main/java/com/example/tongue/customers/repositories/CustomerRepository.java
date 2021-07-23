package com.example.tongue.customers.repositories;

import com.example.tongue.customers.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
