package org.example.Model;

import jakarta.persistence.*;
import org.example.DTO.StatusEnum;

@Entity
@Table(name="status_order")
public class StatusOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_status")
    private Integer id;
    @Column(name = "name")
    private String name;

    public StatusOrder() {
    }

    public StatusOrder(String name) {
        this.name = name;
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
