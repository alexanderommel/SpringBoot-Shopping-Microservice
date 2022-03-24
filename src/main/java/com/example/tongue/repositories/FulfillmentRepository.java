package com.example.tongue.repositories;

import com.example.tongue.domain.checkout.Fulfillment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, Long> {
    Optional<Fulfillment> findByCheckoutId(Long id);
}
