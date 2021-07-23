package com.example.tongue.shippings.repositories;

import com.example.tongue.shippings.models.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRepository extends JpaRepository<Shipping,Long> {
}
