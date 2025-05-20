package com.savelyev.MobileApp.Utils
import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(
    context: Context,
    prefsName: String = "mySettings"
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    companion object {
        const val APP_PREFERENCES_REGISTERED = "user_registered"
        const val APP_PREFERENCES_AUTHORIZED = "user_authorized"
        const val JWT_TOKEN = "jwt_token"
        const val KEY_USER_DATA = "user_data"
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }
    fun removeObject(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).commit()
    }
    fun getLong(key: String, defaultValue: Long = 0): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun getAllData(): String{
        return sharedPreferences.all.toString();
    }
    fun clearAll(){
        return sharedPreferences.edit().clear().apply();
    }
}
