package com.savelyev.MobileApp.Utils

import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import android.util.Base64

object JwtParser {
    private const val TAG = "JwtParser"

    fun isTokenExpired(token: String): Boolean {
        if (!isValidJwtFormat(token)) {
            Log.e(TAG, "Invalid token format")
            return true
        }
        return try {
            // Разбиваем JWT на части
            val parts = token.split(".")

            // Декодируем payload (часть с exp)
            val payload = String(
                Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING),
                StandardCharsets.UTF_8
            )

            // Парсим JSON и проверяем срок
            val json = JSONObject(payload)
            val exp = json.getLong("exp") * 1000
            exp < System.currentTimeMillis()

        } catch (e: Exception) {
            true
        }
    }


    private fun isValidJwtFormat(token: String): Boolean {
        return token.split(".").size == 3
    }
}