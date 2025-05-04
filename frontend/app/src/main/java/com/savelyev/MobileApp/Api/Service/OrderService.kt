package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.OrderStatusDTO
import com.savelyev.MobileApp.Api.Repository.OrderRepository
import com.savelyev.MobileApp.Api.Repository.StatusOrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderService {
    private val orderRepository = OrderRepository()
    private val statusOrderRepository = StatusOrderRepository()

    fun getUserOrderList(userId: Int, callback: (List<OrderDTO>?) -> Unit) {
        val call = orderRepository.getUserOrderList(userId)
        call.enqueue(object : Callback<List<OrderDTO>> {
            override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("OrderList", "Ошибка: ${response.errorBody()?.string()}")
                    callback(null) // TODO: Обработка ошибки
                }
            }

            override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                Log.e("GetData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun addOrder(orderData: OrderDTO, callback: (OrderDTO?) -> Unit) {
        val call = orderRepository.addOrder(orderData)
        call.enqueue(object : Callback<OrderDTO> {
            override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("Order", "Ошибка: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                Log.e("AddData", "Ошибка: ${t.message}")
                callback(null)
            }
        })
    }

    fun updateOrderStatus(orderId: Int, orderStatus: String,  callback: (OrderDTO?) -> Unit) {
        OrderStatusService().getOrderStatus(orderStatus){ orderStatusDTO ->
            if (orderStatusDTO!=null)
            {
                val call = orderRepository.updateOrderStatus(orderId, orderStatusDTO)
                call.enqueue(object : Callback<OrderDTO> {
                    override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                        if (response.isSuccessful) {
                            callback(response.body())
                        } else {
                            Log.e("Order", "Ошибка: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                        Log.e("UpdateData", "Ошибка: ${t.message}")
                        callback(null)
                    }
                })
            }
            else
            {
                callback(null)
            }
        }
    }

    fun deleteByID(id: Int, callback: (Boolean?) -> Unit){
        val call = orderRepository.deleteByID(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(response.isSuccessful)
                } else {
                    Log.e("Order", "Ошибка: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DeleteOrder", "Ошибка сети: ${t.message}")
                callback(false)
            }
        })
    }

}