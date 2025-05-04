package org.example.Repository;

import org.example.Model.TypeBrakes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeBrakesRepository extends JpaRepository<TypeBrakes, Integer> {
    TypeBrakes findByName(String name);
}
