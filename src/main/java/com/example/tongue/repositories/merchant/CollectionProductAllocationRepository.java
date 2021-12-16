package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.CollectionProductAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionProductAllocationRepository
        extends JpaRepository<CollectionProductAllocation,Long> {

    Page<CollectionProductAllocation> findAllByCollection_Id(Long id, Pageable pageable);

}
