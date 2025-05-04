package org.example.Repository;

import org.example.Model.StatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusOrderRepository extends JpaRepository<StatusOrder, Integer> {
    StatusOrder findByName(String name);
}
