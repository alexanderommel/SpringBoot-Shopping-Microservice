package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Modifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier,Long> {
}
