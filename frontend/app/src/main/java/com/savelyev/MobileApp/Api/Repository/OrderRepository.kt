package com.savelyev.MobileApp.Api.Repository

import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.RetrofitClient
import retrofit2.Call

class OrderRepository {
    fun getUserOrderList(userId: Int): Call<List<OrderDTO>> {
        return RetrofitClient.apiService.getUserOrders(userId)
    }

    fun addOrder(orderData: OrderDTO): Call<OrderDTO> {
        return RetrofitClient.apiService.addOrder(orderData)
    }

    fun updateOrderStatus(id: Int, orderStatus: OrderStatusDTO): Call<OrderDTO> {
        return RetrofitClient.apiService.updateOrderStatus(id, orderStatus)
    }

    fun deleteByID(id: Int): Call<Void> {
        return RetrofitClient.apiService.deleteOrderByID(id)
    }
}