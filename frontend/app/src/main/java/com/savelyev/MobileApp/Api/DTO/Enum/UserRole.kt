package com.savelyev.MobileApp.Api.DTO.Enum

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class UserRole(val serverValue: String) {
    USER("user"),
    MANAGER("manager"),
    ADMIN("admin"),
    UNKNOWN("unknown");

    companion object {
        private val roleHierarchy = mapOf(
            USER to setOf(USER),
            MANAGER to setOf(USER, MANAGER),
            ADMIN to setOf(USER, MANAGER, ADMIN)
        )

        @JsonCreator
        fun fromString(value: String): UserRole {
            return entries.find { it.serverValue == value.lowercase() } ?: UNKNOWN
        }
    }

    @JsonValue
    fun toValue(): String = serverValue

    fun hasPermission(requiredRole: UserRole): Boolean {
        return roleHierarchy[this]?.contains(requiredRole) ?: false
    }
}