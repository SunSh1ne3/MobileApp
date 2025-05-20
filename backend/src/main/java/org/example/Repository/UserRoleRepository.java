package org.example.Repository;

import org.example.DTO.UserRoleEnum;
import org.example.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    UserRole findByName(String name);
}
