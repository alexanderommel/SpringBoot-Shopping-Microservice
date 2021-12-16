package com.example.tongue.repositories.merchant;

import com.example.tongue.domain.merchant.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
}
