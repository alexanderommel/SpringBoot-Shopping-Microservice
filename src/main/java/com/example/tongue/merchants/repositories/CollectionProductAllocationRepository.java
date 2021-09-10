package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.CollectionProductAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionProductAllocationRepository
        extends JpaRepository<CollectionProductAllocation,Long> {


}
