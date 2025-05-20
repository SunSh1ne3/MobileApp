package org.example.Model;

import jakarta.persistence.*;
import org.example.DTO.TypeBicycleEnum;

@Entity
@Table(name="type_bicycle")
public class TypeBicycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_type")
    private Integer id;
    @Column(name = "name")
    private String name;

    public TypeBicycle() {
    }
    public TypeBicycle(TypeBicycleEnum name) {
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
    public void getName(String name) {
        this.name = name;
    }
}
