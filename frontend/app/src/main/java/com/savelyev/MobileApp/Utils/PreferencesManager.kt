package com.savelyev.MobileApp.Utils
import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("mySettings", Context.MODE_PRIVATE)

    companion object {
        const  val APP_PREFERENCES_REGISTERED = "user_registered"
        const  val APP_PREFERENCES_AUTHORIZED = "user_authorized"
        const  val JWTTOKEN= "JwtToken"
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getAllData(): String{
        return sharedPreferences.all.toString();
    }
    fun clearAll(){
        return sharedPreferences.edit().clear().apply();
    }
}
