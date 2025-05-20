package com.savelyev.MobileApp.Api.Service

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.savelyev.MobileApp.Api.DTO.Request.AuthData
import com.savelyev.MobileApp.Api.DTO.ErrorResponse.ErrorResponse
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.DTO.UserDTO
import com.savelyev.MobileApp.Api.Repository.UserRepository
import com.savelyev.MobileApp.Utils.TokenManager
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthService {
    private val userRepository = UserRepository()
    private lateinit var tokenManager: TokenManager
    companion object {
        private const val TAG = "AuthService"
    }
    fun initialize(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    fun loginUser(
        authRequest: AuthData,
        onResult: (Boolean, String?) -> Unit) {
        if (!this::tokenManager.isInitialized) {
            onResult(false, "AuthService не инициализирован. Вызовите initialize()")
            return
        }

        val call = userRepository.login(authRequest)
        Log.d(TAG, "Request created: ${call.request().url}")
        call.enqueue(object : Callback<AuthResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d(TAG, "Response received. Code: ${response.code()}")
                try {
                    if (!response.isSuccessful || response.body() == null) {
                        val errorBody = response.errorBody()!!.string()
                        val errorMessage = try {
                            val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                            errorResponse.message ?: "Ошибка авторизации"
                        } catch (e: Exception) {
                            when (response.code()) {
                                401 -> "Неверный логин или пароль"
                                404 -> "Пользователь не найден"
                                else -> "Ошибка сервера: ${response.code()}"
                            }
                        }
                        Log.e(TAG, errorMessage)
                        onResult(false, errorMessage)
                        Log.d(TAG, "Response received. Code: ${response.code()}")
                        return
                    }
                    val token = response.body()!!.token
                    tokenManager.saveToken(token)
                    Log.d(TAG, "Request true: ${tokenManager.getToken()}")
                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, "Неожиданная ошибка: ${e.message}")
                }
            }


            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e(TAG, "Request failed", t)
                val errorMessage = when (t) {
                    is IOException -> "Ошибка сети: ${t.message}"
                    is HttpException -> "Ошибка сервера: ${t.code()}"
                    else -> "Неизвестная ошибка: ${t.message}"
                }
                onResult(false, errorMessage)
            }
        })
    }

//    fun refreshToken(onResult: (Boolean, AuthResponse?) -> Unit) {
//        val refreshToken = TokenManager.getInstance().getToken() ?: run {
//            onResult(false, null)
//            return
//        }
//
//        refreshToken("Bearer $refreshToken", onResult)
//    }
//
//    private fun refreshToken(refreshToken: String, onResult: (Boolean, AuthResponse?) -> Unit) {
//        val call = userRepository.refreshToken("Bearer $refreshToken")
//        call.enqueue(object : Callback<AuthResponse> {
//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onResponse(
//                call: Call<AuthResponse>,
//                response: Response<AuthResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val tokenResponse = response.body()
//                    if (tokenResponse != null) {
//                        val accessExp = JwtParser1.getExpiration(tokenResponse.accessToken)
//                        val refreshExp = tokenResponse.refreshToken?.let { JwtParser1.getExpiration(it) } ?: 0L
//
//                        // Сохраняем новые токены
//                        TokenManager.getInstance().saveTokens(
//                            tokenResponse.accessToken,
//                            tokenResponse.refreshToken ?: "",
//                            accessExp,
//                            refreshExp
//                        )
//                        onResult(true, tokenResponse)
//                    } else {
//                        onResult(false, null)
//                    }
//                } else {
//                    when (response.code()) {
//                        401 -> TokenManager.getInstance().clearToken()
//                    }
//                    onResult(false, null)
//                }
//            }
//
//            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
//                Log.e(TAG, "Refresh token failed", t)
//                onResult(false, null)
//            }
//        })
//    }

    fun registerUser(
        registerRequest: AuthData,
        onResult: (Boolean, String?, UserDTO?) -> Unit) {
        val call = userRepository.registration(registerRequest)
        call.enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    val userDTO = response.body()
                    if (userDTO != null) {
                        onResult(true, null, userDTO)
                    } else {
                        Log.e(TAG, "Registration successful but response body is null")
                        onResult(false, null, null)
                    }

                } else {
                    val errorMessage = when (response.code()) {
                        409 -> "Пользователь с таким номером телефона уже существует."
                        else -> "Ошибка регистрации. Попробуйте еще раз."
                    }
                    Log.e(TAG, errorMessage)
                    onResult(false, errorMessage, null)
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                val errorMessage = when (t) {
                    is IOException -> "Ошибка сети: ${t.message}"
                    is HttpException -> "Ошибка сервера: ${t.code()}"
                    else -> "Неизвестная ошибка: ${t.message}"
                }
                Log.e(TAG, "Registration failed", t)
                onResult(false, errorMessage, null)
            }
        })
    }
}