package com.example.tongue.sales.repositories;

import com.example.tongue.sales.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
