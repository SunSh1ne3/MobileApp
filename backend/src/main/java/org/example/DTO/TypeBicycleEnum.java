package org.example.DTO;

public enum TypeBicycleEnum {
    MOUNTAIN("Горный"),
    SHROSE("Шоссейный"),
    CHILDREN ("Детский"),
    URBAN ("Городской");

    private final String name;

    TypeBicycleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
