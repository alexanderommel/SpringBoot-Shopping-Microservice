package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Discount;
import com.example.tongue.merchants.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
    Page<Discount> findAll(Pageable pageable);
    public List<Discount> findAllByCreatedAt(Instant instant);
}
