package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<Collection,Long> {
}
