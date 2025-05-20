package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.UserDTO
import com.savelyev.MobileApp.Api.Repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserService {
    fun getUser(numberPhone: String, onSuccess: (UserDTO) -> Unit, onError: (String) -> Unit) {
        val call = UserRepository().getUserByPhone(numberPhone)
        Log.d("UserAPI", "Starting request to ${call.request().url}")
        Log.d("UserAPI", "Headers: ${call.request().headers.toMultimap()}")
        call.enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                Log.d("UserAPI", "Received response from ${call.request().url}")
                Log.d("UserAPI", "Response code: ${response.code()}")
                Log.d("UserAPI", "Response headers: ${response.headers().toMultimap()}")
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Log.d("UserAPI", "Request successful, body: ${response.body()}")
                        onSuccess(user)
                    } else {
                        Log.e("UserAPI", "Неверный формат данных пользователя, Error response: ${response.code()} - ${response.errorBody()?.string()}")
                        onError("Неверный формат данных пользователя")
                    }
                } else {
                    Log.e("UserAPI", "Error response: ${response.code()} - ${response.errorBody()?.string()}")
                    onError("Ошибка сервера: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                Log.e("UserAPI", "Request failed to ${call.request().url}", t)
                onError("Ошибка сети: ${t.message}")
            }
        })
    }

    fun getUserById(userID: Int, callback: (UserDTO?, String?) -> Unit) {
        val call = UserRepository().getUserById(userID)
        call.enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        callback(user, null)
                    } else {
                        callback(null, "Неверный формат данных пользователя")
                    }
                } else {
                    callback(null, "Ошибка сервера: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                callback(null, "Ошибка сети: ${t.message}")
            }
        })
    }
}