package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.Modifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModifierRepository extends JpaRepository<Modifier,Long> {
    List<Modifier> findAllByGroupModifier_Id(Long id);
}
