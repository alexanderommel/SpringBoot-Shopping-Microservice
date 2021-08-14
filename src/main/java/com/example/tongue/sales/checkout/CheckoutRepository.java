package com.example.tongue.sales.checkout;

import com.example.tongue.merchants.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRepository extends JpaRepository<Checkout,Long> {
    Page<Checkout> findAll(Pageable pageable);
}
