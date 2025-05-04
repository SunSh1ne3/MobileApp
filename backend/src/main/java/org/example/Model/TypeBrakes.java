package org.example.Model;

import jakarta.persistence.*;

@Entity
@Table(name="type_brakes")
public class TypeBrakes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_type")
    private Integer id;
    @Column(name = "name")
    private String name;

    public TypeBrakes() {
    }
    public TypeBrakes(String name) {
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
    public void getName(String name) {
        this.name = name;
    }
}
