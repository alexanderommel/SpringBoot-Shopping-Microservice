package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.StoreVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface StoreVariantRepository extends JpaRepository<StoreVariant,Long> {
}
