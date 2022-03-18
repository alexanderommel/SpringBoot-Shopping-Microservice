package com.example.tongue.repositories;

import com.example.tongue.domain.checkout.Fulfillment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, Long> {
}
