package org.example.Model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name="bicycles")
public class Bicycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_bicycle")
    private Integer id;
    private String name;
    private Double weight;
    private String frameMaterial;
    private Integer wheelSize;

    private Integer typeBrakes;
    private Integer typeBicycle;

    private Integer age;
    private Integer numberSpeeds;
    private Integer maximumLoad;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "bicycle_bicycle_image",
            joinColumns = { @JoinColumn(name = "bicycle_id") },
            inverseJoinColumns = { @JoinColumn(name = "image_id") }
    )
    private List<BicycleImage> images;


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

    public Integer getWheelSize() {
        return wheelSize;
    }
    public void setWheelSize(Integer wheelSize) {
        this.wheelSize = wheelSize;
    }

    public Integer getTypeBrakes() {
        return typeBrakes;
    }
    public void setTypeBrakes(Integer typeBrakes) {
        this.typeBrakes = typeBrakes;
    }

    public Integer getTypeBicycle() {
        return typeBicycle;
    }
    public void setTypeBicycle(Integer typeBicycle) {
        this.typeBicycle = typeBicycle;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getNumberSpeeds() {
        return numberSpeeds;
    }
    public void setNumberSpeeds(Integer numberSpeeds) {
        this.numberSpeeds = numberSpeeds;
    }

    public Integer getMaximumLoad() {
        return maximumLoad;
    }
    public void setMaximumLoad(Integer maximumLoad) {
        this.maximumLoad = maximumLoad;
    }

    public List<BicycleImage> getImages() {
        return images;
    }
    public void setImages(List<BicycleImage> images) {
        this.images = images;
    }

//    public List<Integer> getImages() {
//        return images;
//    }
//    public void setImages(List<Integer> images) {
//        this.images = images;
//    }
}
