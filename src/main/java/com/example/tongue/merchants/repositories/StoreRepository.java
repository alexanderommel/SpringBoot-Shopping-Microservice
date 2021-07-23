package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store,Long> {
}
