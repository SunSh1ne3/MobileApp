package com.savelyev.MobileApp.Api.Service

import com.savelyev.MobileApp.Api.DTO.Request.AuthData
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.DTO.Response.AuthResponse
import com.savelyev.MobileApp.Api.DTO.TypeBicycleDTO
import com.savelyev.MobileApp.Api.DTO.TypeBrakesDTO
import com.savelyev.MobileApp.Api.DTO.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    //Auth
    @GET("api/v1/users")
    fun getUsers(): Call<List<AuthData>>

    @GET("api/v1/users/get/{numberPhone}")
    fun getUserByNumberPhone(@Path("numberPhone") numberPhone: String): Call<UserDTO>

    @GET("api/v1/users/get/id/{userId}")
    fun getUserById(@Path("userId") userId: Int): Call<UserDTO>

    @POST("/api/v1/auth/registration")
    fun registration(@Body userData: AuthData) : Call<UserDTO>

    @POST("/api/v1/auth/login")
    fun login(@Body authData: AuthData): Call<AuthResponse>

    @POST("/api/v1/auth/refresh-token")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<AuthResponse>

    //Bicycle
    @GET("api/v1/bicycle")
    fun getBicycles(): Call<List<BikeDTO>>

    @POST("api/v1/bicycle")
    fun addBicycle(@Body bicycleData: BikeDTO): Call<BikeDTO>

    @GET("api/v1/bicycle/name/{name}")
    fun getBicycle(@Path("name") name: String?): Call<BikeDTO>

    @GET("api/v1/bicycle/id/{id}")
    fun getBicycle(@Path("id") id: Int?): Call<BikeDTO>

    @GET("api/v1/bicycle/parameters/brakes/{id_brake}")
    fun getTypeBrakeBicycle(@Path("id_brake") brakeID: Int?): Call<TypeBrakesDTO>

    @GET("api/v1/bicycle/parameters/types/{id_type}")
    fun getTypeBicycle(@Path("id_type") typeID: Int?): Call<TypeBicycleDTO>

    @GET("api/v1/bicycle/parameters/types")
    fun getTypesBicycle(): Call<List<TypeBicycleDTO>>

    @GET("api/v1/bicycle/parameters/brakes")
    fun getTypesBrakesBicycle(): Call<List<TypeBrakesDTO>>

    //Order
    @GET("/api/v1/order/user/{id_user}/orders")
    fun getUserOrders(@Path("id_user") userID: Int): Call<List<OrderDTO>>

    @GET("/api/v1/order/user/{id_user}/active")
    fun getActiveOrder(@Path("id_user") userID: Int): Call<List<OrderDTO>>

    @GET("/api/v1/order/user/{id_user}/issued")
    fun getIssuedUserOrder(@Path("id_user") userID: Int): Call<List<OrderDTO>>

    @GET("/api/v1/order/user/{id_user}/awaitingConfirm")
    fun getAwaitingConfirmUserOrder(@Path("id_user") userID: Int): Call<List<OrderDTO>>

    @GET("/api/v1/order/issued")
    fun getIssuedOrder(): Call<List<OrderDTO>>

    @GET("/api/v1/order/status/{nameStatus}")
    fun getStatusByName(@Path("nameStatus") nameStatus: String): Call<OrderStatusDTO>

    @POST("/api/v1/order")
    fun addOrder(@Body orderData: OrderDTO): Call<OrderDTO>

    @PATCH("/api/v1/order/{id}/status")
    fun updateOrderStatus(@Path("id") id: Int, @Body orderData: OrderStatusDTO): Call<OrderDTO>

    @DELETE("/api/v1/order/{id}")
    fun deleteOrderByID(@Path("id") id: Int): Call<Void>
}
