package org.example.Model;
import jakarta.persistence.*;

/// Сущность для Юзера
@Entity
@Table (name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_user")

    private Long id;
    private String username;
    private String password;
    private String numberPhone;
    private Long typeOrder;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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

    public Long getTypeOrder() { return typeOrder; }
    public void setTypeOrder(Long typeOrder) { this.typeOrder = typeOrder; }
}
