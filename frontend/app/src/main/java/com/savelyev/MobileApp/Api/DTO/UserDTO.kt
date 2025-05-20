package com.savelyev.MobileApp.Api.DTO

import com.fasterxml.jackson.annotation.JsonProperty
import com.savelyev.MobileApp.Api.DTO.Enum.UserRole

data class UserDTO(
    @JsonProperty("id") val id: Int,
    @JsonProperty("username") val username: String,
    @JsonProperty("numberPhone") val numberPhone: String,
    @JsonProperty("userRole") val userRole: String
) {
    fun toUserRole(): UserRole {
        return UserRole.fromString(userRole)
    }
}
