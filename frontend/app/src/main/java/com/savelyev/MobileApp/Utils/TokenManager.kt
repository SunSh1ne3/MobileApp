package com.savelyev.MobileApp.Utils

import android.content.Context
import android.util.Log

class TokenManager(context: Context) {
    private var tokenPreferences: PreferencesManager = PreferencesManager(context)
    private val tag = "TokenManager"


    fun saveToken(token: String) {
        tokenPreferences.saveString(PreferencesManager.JWT_TOKEN, token).also {
            Log.d(tag, "Token saved: ${token.take(5)}...")
        }
    }

    fun getData() {
        Log.d(tag, "AllData: ${ tokenPreferences.getAllData()}...")
    }

    fun getToken(): String? {
        return tokenPreferences.getString(PreferencesManager.JWT_TOKEN)
    }

    fun clearToken() {
        tokenPreferences.removeObject(PreferencesManager.JWT_TOKEN)
        Log.d(tag, "Token cleared")
    }

    companion object {
        @Volatile private var instance: TokenManager? = null

        fun init(context: Context) {
            instance ?: synchronized(this) {
                instance = TokenManager(context.applicationContext)
            }
        }

        fun getInstance(): TokenManager {
            return instance ?: throw IllegalStateException("TokenManager not initialized! Call TokenManager.init() first.")
        }
    }
}
