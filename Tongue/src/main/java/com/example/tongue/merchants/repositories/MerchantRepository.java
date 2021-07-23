package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant,Long> {
}
