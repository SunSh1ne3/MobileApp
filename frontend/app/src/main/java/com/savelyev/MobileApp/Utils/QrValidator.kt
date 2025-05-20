package com.savelyev.MobileApp.Utils

import kotlinx.serialization.json.Json

object QrValidator {
    fun isValidQrJson(content: String): Boolean {
        return try {
            content.isNotBlank() &&
                    content.startsWith("{") &&
                    content.endsWith("}") &&
                    Json.parseToJsonElement(content) != null
        } catch (e: Exception) {
            false
        }
    }
}