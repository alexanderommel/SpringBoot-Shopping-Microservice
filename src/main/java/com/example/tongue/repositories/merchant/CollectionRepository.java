package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,Long> {
    Page<Collection> findAllByStoreVariantId(Long id, Pageable pageable);
}
