package org.example.Repository;

import org.example.Model.Bicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BicycleRepository extends JpaRepository<Bicycle, Integer> {
    Optional<Bicycle> findByName(String name);

    @Query("SELECT b FROM Bicycle b LEFT JOIN FETCH b.images")
    List<Bicycle> findAllWithImages();

}
