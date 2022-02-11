package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,Long> {
    List<Collection> findAllByStoreVariantId(Long id);
}
