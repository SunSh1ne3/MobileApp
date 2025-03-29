package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.Repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthService {

    private val userRepository = UserRepository()

    fun loginUser(authRequest: AuthData, onResult: (Boolean, String?) -> Unit) {
        val call = userRepository.login(authRequest)
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("AuthUser", "Response code: ${response.code()}")
                Log.d("AuthUser", "Response body: ${response.body()}")
                Log.d("AuthUser", "Error body: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    val jwtToken = response.body()?.jwtToken
                    onResult(true, jwtToken)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthUser", "Ошибка: ${response.code()} - ${errorBody}")

                    when (response.code()) {
                        401 -> onResult(false, "Неправильные данные, перепроверьте.")
                        404 -> onResult(false, "Пользователь не найден.")
                        else -> onResult(false, "Ошибка авторизации. Попробуйте еще раз.")
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e("AuthUser", "Ошибка: ${t.message}")
                when (t) {
                    is IOException -> {
                        Log.e("AuthUser", "Ошибка сети. Проверьте подключение к интернету")
                        onResult(false, "Ошибка сети. Проверьте подключение к интернету. ${t.message}")
                    }

                    is HttpException -> {
                        Log.e("AuthUser", "Ошибка сервера")
                        onResult(false, "Ошибка сервера. Код: ${t.code()}")
                    }

                    else -> {
                        Log.e("AuthUser", "Неизвестная ошибка")
                        onResult(false, "Неизвестная ошибка: ${t.message}")
                    }
                }
            }
        })
    }

    fun registerUser( registerRequest: AuthData, onResult: (Boolean, String?) -> Unit) {
        val call = userRepository.registration(registerRequest)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("RegisterUser", "Ошибка: ${response.code()} - ${errorBody}")

                    when (response.code()) {
                        409 -> onResult(false, "Пользователь с таким номером телефона уже существует.")
                        else -> onResult(false, "Ошибка регистрации. Попробуйте еще раз.")
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("RegisterUser", "Ошибка: ${t.message}")
                onResult(false, "Ошибка сети. Проверьте подключение к интернету.")
            }
        })
    }
}