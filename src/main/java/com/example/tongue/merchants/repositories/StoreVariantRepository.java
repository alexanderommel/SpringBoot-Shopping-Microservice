package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.enumerations.StoreVariantType;
import com.example.tongue.merchants.models.StoreVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public interface StoreVariantRepository extends JpaRepository<StoreVariant,Long> {
    Page<StoreVariant> findAllByStoreFoodType(StoreVariantType storeVariantType,
                                                    Pageable pageable);
}
