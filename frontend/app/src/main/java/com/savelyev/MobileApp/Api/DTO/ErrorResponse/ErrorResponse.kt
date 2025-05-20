package com.savelyev.MobileApp.Api.DTO.ErrorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val status: Int? = null
) {
    constructor() : this(null, null, null) // Для парсинга
}
