package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
    Page<Discount> findAll(Pageable pageable);
    List<Discount> findAllByStoreVariantId(Long id);
}
