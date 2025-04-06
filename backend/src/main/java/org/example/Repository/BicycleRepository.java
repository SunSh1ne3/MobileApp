package org.example.Repository;

import org.example.Model.Bicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BicycleRepository extends JpaRepository<Bicycle, Long> {
    Optional<Bicycle> findByName(String name);
}
