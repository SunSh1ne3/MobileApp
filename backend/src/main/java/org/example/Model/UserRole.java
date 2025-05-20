package org.example.Model;

import jakarta.persistence.*;
import org.example.DTO.UserRoleEnum;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="user_role")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_role")
    private Integer id;

    @Column(name = "name", unique = true)
    private String name;

    public UserRole() {}
    public UserRole(UserRoleEnum name) {
        this.name = name.getName();
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
