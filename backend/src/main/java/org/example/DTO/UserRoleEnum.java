package org.example.DTO;

public enum UserRoleEnum {
    USER("user"),
    MANAGER("manager"),
    ADMIN("admin");
    private final String name;

    UserRoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static UserRoleEnum fromString(String value) {
        for (UserRoleEnum role : values()) {
            if (role.name.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
