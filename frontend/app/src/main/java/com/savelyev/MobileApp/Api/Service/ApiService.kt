package com.savelyev.MobileApp.Api.Service

import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.DTO.TypeBicycleDTO
import com.savelyev.MobileApp.Api.DTO.TypeBrakesDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    //Auth
    @GET("api/v1/users")
    fun getUsers(): Call<List<AuthData>>

    @POST("/api/v1/auth/registration")
    fun registration(@Body userData: AuthData) : Call<String>

    @POST("/api/v1/auth/login")
    fun login(@Body userData: AuthData) : Call<AuthResponse>

    //Bicycle
    @GET("api/v1/bicycle")
    fun getBicycles(): Call<List<BikeDTO>>

    @GET("api/v1/bicycle/name/{name}")
    fun getBicycle(@Path("name") name: String?): Call<BikeDTO>

    @GET("api/v1/bicycle/id/{id}")
    fun getBicycle(@Path("id") id: Int?): Call<BikeDTO>

    @GET("api/v1/bicycle/parameters/brakes/{id_brake}")
    fun getTypeBrakesBicycle(@Path("id_brake") id_brake: Int?): Call<TypeBrakesDTO>

    @GET("api/v1/bicycle/parameters/type/{id_type}")
    fun getTypeBicycle(@Path("id_type") id_type: Int?): Call<TypeBicycleDTO>

    //Order
    @GET("/api/v1/order/user/{id_user}/orders")
    fun getUserOrders(@Path("id_user") id_user: Int): Call<List<OrderDTO>>

    @GET("/api/v1/order/status/{nameStatus}")
    fun getStatusByName(@Path("nameStatus") nameStatus: String): Call<OrderStatusDTO>

    @POST("/api/v1/order")
    fun addOrder(@Body orderData: OrderDTO): Call<OrderDTO>

    @PATCH("/api/v1/order/{id}/status")
    fun updateOrderStatus(@Path("id") id: Int, @Body orderData: OrderStatusDTO): Call<OrderDTO>

    @DELETE("/api/v1/order/{id}")
    fun deleteOrderByID(@Path("id") id: Int): Call<Void>
}
