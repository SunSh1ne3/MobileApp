package com.savelyev.MobileApp.Api.Service

import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/v1/users")
    fun getUsers(): Call<AuthData>

    @POST("/api/v1/auth/registration")
    fun registration(@Body userData: AuthData) : Call<String>;

    @POST("/api/v1/auth/login")
    fun login(@Body userData: AuthData) : Call<AuthResponse>;
}
