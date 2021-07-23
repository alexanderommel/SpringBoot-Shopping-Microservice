package com.example.tongue.locations.repositories;

import com.example.tongue.locations.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location,Long> {
}
