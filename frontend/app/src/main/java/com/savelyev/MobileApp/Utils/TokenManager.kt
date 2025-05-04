package com.savelyev.MobileApp.Utils

import android.content.Context

class TokenManager(context: Context) {
    private var tokenPreferences = PreferencesManager(context)

    fun saveToken(token: String) {
        tokenPreferences.saveString(PreferencesManager.JWTTOKEN, token).also {
            android.util.Log.d("TokenManager", "Token saved: ${token.take(5)}...")
        }
    }
    fun getToken() :String?{
        val token =  tokenPreferences.getString(PreferencesManager.JWTTOKEN)
        return token?.takeIf { it.isNotBlank() }
    }

    fun clearToken() {
        tokenPreferences.removeString(PreferencesManager.JWTTOKEN).also {
            android.util.Log.d("TokenManager", "Token cleared")
        }
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
