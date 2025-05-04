package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.Repository.StatusOrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OrderStatusService {
    private val statusOrderRepository = StatusOrderRepository()

    fun getOrderStatus(orderStatus: String,  callback: (OrderStatusDTO?) -> Unit) {
        val newStatus = statusOrderRepository.getStatusByName(orderStatus)
        newStatus.enqueue(object : Callback<OrderStatusDTO> {
            override fun onResponse(call: Call<OrderStatusDTO>, response: Response<OrderStatusDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("Order", "Ошибка: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<OrderStatusDTO>, t: Throwable) {
                Log.e("GetStatus", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun getStatus(orderStatus: String, callback: (OrderStatusDTO?) -> Unit) {
        getOrderStatus(orderStatus) { orderStatusDTO ->
            callback(orderStatusDTO)
        }
    }
}