package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import retrofit2.Call

class UserRepository {
    fun registration (authData: AuthData): Call<String> {
        return RetrofitClient.apiService.registration( authData )
    }

    fun login (authData: AuthData): Call<AuthResponse> {
        return RetrofitClient.apiService.login( authData )
    }
}