package org.example.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "bicycle_image")
public class BicycleImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}