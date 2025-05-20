package com.savelyev.MobileApp.Api.DTO.Enum

import com.fasterxml.jackson.annotation.JsonValue

enum class StatusEnum(private val status: String) {
    NEW("Новый"),
    ISSUED ("Выдано"),
    AWAITING_PAYMENT ("Ожидает оплаты"),
    PAID("Оплачен"),
    COMPLETED("Завершен"),
    AWAITING_CONFIRMATION("Ожидает подтверждения"),
    CANCELLED("Отменен");

    @JsonValue
    fun toValue(): String = status

    companion object {
        fun fromStatus(status: String): String {
            return try {
                valueOf(status).toValue()
            } catch (e: IllegalArgumentException) {
                "Неизвестный статус"
            }
        }
    }
}