package com.example.tongue.integrations.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerReplicationRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByUserId(String id);
}
