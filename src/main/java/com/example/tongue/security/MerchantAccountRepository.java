package com.example.tongue.security;

import com.example.tongue.security.domain.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantAccountRepository  extends JpaRepository<MerchantAccount,String> {
}
