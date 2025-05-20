package com.savelyev.MobileApp.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.savelyev.MobileApp.Api.DTO.UserDTO
import com.savelyev.MobileApp.Api.DTO.Enum.UserRole
import com.savelyev.MobileApp.Api.Service.UserService
import com.savelyev.MobileApp.Utils.PreferencesManager.Companion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
object UserManager {
    private lateinit var preferencesManager: PreferencesManager
    private var currentUser: UserDTO? = null
    private const val TAG = "UserManager"
    private var isInitialized = false
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        isInitialized = true
        preferencesManager = PreferencesManager(context)
        loadUser()
    }

    fun isInitialized(): Boolean = ::context.isInitialized

    fun setCurrentUser(user: UserDTO) {
        currentUser = user
        saveUser()
        notifyUserChanged()
    }

    fun getCurrentUser(): UserDTO? {
        require(isInitialized) { "UserManager not initialized. Call init() first." }
        return currentUser
    }

    fun clearUser() {
        currentUser = null
        preferencesManager.removeObject(Companion.KEY_USER_DATA)
        notifyUserChanged()
    }

    fun isAdminOrManager(): Boolean {
        val user = getCurrentUser()
        return user?.userRole == UserRole.ADMIN.toValue() || user?.userRole == UserRole.MANAGER.toValue()
    }

    fun isManager(): Boolean {
        val user = getCurrentUser()
        return user?.userRole == UserRole.MANAGER.toValue()
    }

    fun isAdmin(): Boolean {
        val user = getCurrentUser()
        return user?.userRole == UserRole.ADMIN.toValue()
    }

    private fun saveUser() {
        currentUser?.let { user ->
            preferencesManager.saveString(
                Companion.KEY_USER_DATA,
                Gson().toJson(user)
            )
        }
    }

    private fun loadUser() {
        val userJson = preferencesManager.getString(Companion.KEY_USER_DATA)
        currentUser = userJson?.let {
            Gson().fromJson(it, UserDTO::class.java)
        }
    }

    private val userListeners = mutableListOf<(UserDTO?) -> Unit>()

    fun addUserChangeListener(listener: (UserDTO?) -> Unit) {
        userListeners.add(listener)
    }

    private fun notifyUserChanged() {
        userListeners.forEach { it(currentUser) }
    }

    fun fetchAndSaveUser(
        numberPhone: String,
        onComplete: (success: Boolean) -> Unit
    ) {
        UserService().getUser(
            numberPhone,
            onSuccess = { user ->
                CoroutineScope(Dispatchers.Main).launch {
                    setCurrentUser(user)
                    onComplete(true)
                }
            },
            onError = { error ->
                CoroutineScope(Dispatchers.Main).launch {
                    Log.e(TAG, "Ошибка загрузки: $error")
                    PushManager.showToast("Ошибка обновления данных")
                    onComplete(false)
                }
            }
        )
    }

}