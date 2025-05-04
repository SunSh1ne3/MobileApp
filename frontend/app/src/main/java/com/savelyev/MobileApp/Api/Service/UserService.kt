package com.savelyev.MobileApp.Api.Service

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.savelyev.MobileApp.Utils.PreferencesManager

class UserService (context: Context){
    private var token = PreferencesManager(context).getString(PreferencesManager.JWTTOKEN).toString()
    fun extractUserIdFromToken(): Int {
        return try {
            val decodedJWT: DecodedJWT = JWT.decode(token)
            decodedJWT.getClaim("userId").asInt()
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    fun getToken(): String {
        return token
    }
}