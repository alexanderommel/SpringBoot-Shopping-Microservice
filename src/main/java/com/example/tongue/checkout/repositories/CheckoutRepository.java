package com.example.tongue.checkout.repositories;

import com.example.tongue.checkout.models.Checkout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRepository extends JpaRepository<Checkout,Long> {
    Page<Checkout> findAll(Pageable pageable);
}
