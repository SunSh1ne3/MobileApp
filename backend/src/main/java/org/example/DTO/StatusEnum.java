package org.example.DTO;

public enum StatusEnum {
    ACTIVE("Активный"),
    NEW("Новый"),
    PROGRESS("В процессе"),
    PAID ("Оплачен"),
    AWAITING_PAYMENT ("Ожидает оплаты"),
    COMPLETED ("Завершен");

    private final String name;

    StatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}