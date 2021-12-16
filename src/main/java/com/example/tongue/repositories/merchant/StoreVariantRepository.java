package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.enumerations.StoreVariantType;
import com.example.tongue.domain.merchant.StoreVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface StoreVariantRepository extends JpaRepository<StoreVariant,Long> {
    Page<StoreVariant> findAllByStoreFoodType(StoreVariantType storeVariantType,
                                                    Pageable pageable);
}
