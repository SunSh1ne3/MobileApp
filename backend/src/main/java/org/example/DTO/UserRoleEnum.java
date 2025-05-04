package org.example.DTO;

public enum UserRoleEnum {
    USER("Пользователь"),
    MANAGER("Менеджер"),
    ADMIN("Администратор");

    private final String name;

    UserRoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
