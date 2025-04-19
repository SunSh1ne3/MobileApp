package com.savelyev.MobileApp.Api.Service

import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.DTO.Response.BikeDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/users")
    fun getUsers(): Call<List<AuthData>>

    @POST("/api/v1/auth/registration")
    fun registration(@Body userData: AuthData) : Call<String>

    @POST("/api/v1/auth/login")
    fun login(@Body userData: AuthData) : Call<AuthResponse>

    @GET("api/v1/bicycle")
    fun getBicycles(): Call<List<BikeDTO>>

    @GET("api/v1/bicycle/{id}")
    fun getBicycle(@Path("id") id: Int?): Call<BikeDTO>

    @GET("api/v1/bicycle/{name}")
    fun getBicycle(@Path("name") name: String?): Call<BikeDTO>
}
