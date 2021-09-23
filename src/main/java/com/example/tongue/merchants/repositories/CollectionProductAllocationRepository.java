package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.CollectionProductAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionProductAllocationRepository
        extends JpaRepository<CollectionProductAllocation,Long> {

    List<CollectionProductAllocation> findAllByCollection_Id(Long id);

}
