package org.example.Model;

import jakarta.persistence.*;
import org.example.DTO.UserRoleEnum;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;
    private String username;
    private String password;
    @Column(name = "number_phone", unique = true)
    private String numberPhone;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_role", referencedColumnName = "id_role")
    private UserRole userRole;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public UserRole getUserRole() {
        return userRole;
    }
    @Transient
    public String getRoleName() {
        return userRole != null ? userRole.getName() : null;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
