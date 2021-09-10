package com.example.tongue.merchants.repositories;

import com.example.tongue.merchants.models.GroupModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupModifierRepository extends JpaRepository<GroupModifier,Long> {
}
