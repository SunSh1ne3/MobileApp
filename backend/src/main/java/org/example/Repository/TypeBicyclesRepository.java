package org.example.Repository;

import org.example.Model.TypeBicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeBicyclesRepository extends JpaRepository<TypeBicycle, Integer> {
    TypeBicycle findByName(String name);
}
