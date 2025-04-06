package org.example.Model;

import jakarta.persistence.*;

@Entity
@Table(name="bicycles")
public class Bicycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_bicycle")
    private Long id;
    private String name;
    private Double weight;
    private String frameMaterial;
    private Long wheelSize;

    private Long typeBrakes;
    private Long typeBicycle;
    private Long age;

    private Long numberSpeeds;
    private Long maximumLoad;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFrameMaterial() {
        return frameMaterial;
    }
    public void setFrameMaterial(String frameMaterial) {
        this.frameMaterial = frameMaterial;
    }

    public Long getWheelSize() {
        return wheelSize;
    }
    public void setWheelSize(Long wheelSize) {
        this.wheelSize = wheelSize;
    }

    public Long getTypeBrakes() {
        return typeBrakes;
    }
    public void setTypeBrakes(Long typeBrakes) {
        this.typeBrakes = typeBrakes;
    }

    public Long getTypeBicycle() {
        return typeBicycle;
    }
    public void setTypeBicycle(Long typeBicycle) {
        this.typeBicycle = typeBicycle;
    }

    public Long getAge() {
        return age;
    }
    public void setAge(Long age) {
        this.age = age;
    }

    public Long getNumberSpeeds() {
        return numberSpeeds;
    }
    public void setNumberSpeeds(Long numberSpeeds) {
        this.numberSpeeds = numberSpeeds;
    }

    public Long getMaximumLoad() {
        return maximumLoad;
    }
    public void setMaximumLoad(Long maximumLoad) {
        this.maximumLoad = maximumLoad;
    }
}
