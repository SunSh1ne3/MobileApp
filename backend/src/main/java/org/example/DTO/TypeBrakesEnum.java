package org.example.DTO;

public enum TypeBrakesEnum {
    DISK("Дисковые"),
    DISK_MECHANICAL("Дисковые механические");

    private final String name;

    TypeBrakesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

