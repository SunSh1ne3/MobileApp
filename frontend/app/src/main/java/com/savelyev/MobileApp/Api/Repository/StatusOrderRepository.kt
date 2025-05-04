package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.RetrofitClient
import retrofit2.Call

class StatusOrderRepository {
    fun getStatusByName(statusName: String): Call<OrderStatusDTO> {
        return RetrofitClient.apiService.getStatusByName(statusName)
    }
}