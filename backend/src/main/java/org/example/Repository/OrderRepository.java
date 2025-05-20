package org.example.Repository;

import org.example.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{
    List<Order> findByUserId(Integer statusId);

    List<Order> findByUserIdAndStatusIn(Integer userId, List<String> status);

    List<Order> findByStatusIn(List<String> status);

//    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.statusId = :statusId")
//    List<Order> findByUserIdAndStatusID(@Param("userId") Integer userId, @Param("statusId") Integer statusId);
//
//    @Query("SELECT o FROM Order o WHERE o.statusId = " +
//            "(SELECT s.id FROM StatusEnum s WHERE s.name = :status)")
//    List<Order> findByStatusID(@Param("status") String status);
}