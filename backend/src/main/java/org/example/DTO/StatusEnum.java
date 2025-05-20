package org.example.DTO;

import java.util.Arrays;

public enum StatusEnum {
    NEW("Новый"),
    PAID("Оплачен"),
    AWAITING_PAYMENT ("Ожидает оплаты"),
    COMPLETED("Завершен"),
    AWAITING_CONFIRMATION("Ожидает подтверждения"),
    ISSUED("Выдано");
    private final String name;

    StatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StatusEnum fromName(String name) {
        return Arrays.stream(values())
                .filter(status -> status.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный статус: " + name));
    }
}