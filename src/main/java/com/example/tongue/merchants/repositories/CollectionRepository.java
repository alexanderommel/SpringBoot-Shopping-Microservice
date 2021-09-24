package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,Long> {
    Page<Collection> findAllByStoreVariantId(Long id, Pageable pageable);
}
