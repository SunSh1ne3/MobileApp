package org.example.Repository;

import org.example.Model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @EntityGraph(attributePaths = "userRole")
    Optional<User> findByNumberPhone(String numberPhone);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRole")
    List<User> findAllWithUserRoles();
}
