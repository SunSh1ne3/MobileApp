package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.Api.DTO.Request.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.DTO.UserDTO
import retrofit2.Call

class UserRepository {
    fun registration (authData: AuthData): Call<UserDTO> {
        return RetrofitClient.apiService.registration( authData )
    }

    fun refreshToken(authorization: String): Call<AuthResponse>{
        return RetrofitClient.apiService.refreshToken( authorization )
    }

    fun login (authData: AuthData): Call<AuthResponse> {
        return RetrofitClient.apiService.login( authData )
    }

    fun getUserByPhone (numberPhone: String): Call<UserDTO> {
        return RetrofitClient.apiService.getUserByNumberPhone( numberPhone )
    }

    fun getUserById (userID: Int): Call<UserDTO> {
        return RetrofitClient.apiService.getUserById(userID)
    }
}