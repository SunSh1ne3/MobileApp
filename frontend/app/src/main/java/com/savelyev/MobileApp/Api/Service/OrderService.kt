package com.savelyev.MobileApp.Api.Service

import android.util.Log
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.Repository.OrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderService {
    private val orderRepository = OrderRepository()
    private val tag = "OrderService"

    fun getUserOrderList(userId: Int, callback: (List<OrderDTO>?) -> Unit) {
        Log.d(tag, "[getUserOrderList] Starting for user ID: $userId")

        try {
            val call = orderRepository.getUserOrderList(userId)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<List<OrderDTO>> {
                override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                    Log.d(tag, "[getUserOrderList] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val orders = response.body()
                            Log.d(tag, "Received ${orders?.size ?: 0} orders")
                            callback(orders)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[getUserOrderList] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    Log.e(tag, "[getUserOrderList] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[getUserOrderList] Unexpected error", e)
            callback(null)
        }
    }

    fun getUserActiveOrders(userId: Int, callback: (List<OrderDTO>?) -> Unit) {
        Log.d(tag, "[getUserActiveOrders] Starting for user ID: $userId")
        try {
            val call = orderRepository.getActiveUserOrderList(userId)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<List<OrderDTO>> {
                override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                    Log.d(tag, "[getUserActiveOrders] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val orders = response.body()
                            Log.d(tag, "Received ${orders?.size ?: 0} orders")
                            Log.d(tag, "ReceivedBody ${response.body()}")
                            callback(orders)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[getUserActiveOrders] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    Log.e(tag, "[getUserActiveOrders] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[getUserActiveOrders] Unexpected error", e)
            callback(null)
        }
    }

    fun getUserIssuedOrders(userId: Int, callback: (List<OrderDTO>?) -> Unit) {
        Log.d(tag, "[getUserIssuedOrders] Starting for user ID: $userId")
        try {
            val call = orderRepository.getIssueUserOrderList(userId)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<List<OrderDTO>> {
                override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                    Log.d(tag, "[getUserIssuedOrders] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val orders = response.body()
                            Log.d(tag, "Received ${orders?.size ?: 0} orders")
                            Log.d(tag, "ReceivedBody ${response.body()}")
                            callback(orders)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[getUserIssuedOrders] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    Log.e(tag, "[getUserIssuedOrders] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[getUserIssuedOrders] Unexpected error", e)
            callback(null)
        }
    }

    fun getUserAwaitingConfirmOrders(userId: Int, callback: (List<OrderDTO>?) -> Unit) {
        Log.d(tag, "[getUserAwaitingConfirmOrders] Starting for user ID: $userId")
        try {
            val call = orderRepository.getAwaitingConfirmUserOrderList(userId)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<List<OrderDTO>> {
                override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                    Log.d(tag, "[getUserAwaitingConfirmOrders] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val orders = response.body()
                            Log.d(tag, "Received ${orders?.size ?: 0} orders")
                            Log.d(tag, "ReceivedBody ${response.body()}")
                            callback(orders)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[getUserAwaitingConfirmOrders] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    Log.e(tag, "[getUserAwaitingConfirmOrders] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[getUserAwaitingConfirmOrders] Unexpected error", e)
            callback(null)
        }
    }

    fun getIssuedOrders(callback: (List<OrderDTO>?) -> Unit) {
        Log.d(tag, "[getIssuedOrders] Starting")
        try {
            val call = orderRepository.getIssuedOrderList()
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<List<OrderDTO>> {
                override fun onResponse(call: Call<List<OrderDTO>>, response: Response<List<OrderDTO>>) {
                    Log.d(tag, "[getIssuedOrders] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val orders = response.body()
                            Log.d(tag, "Received ${orders?.size ?: 0} orders")
                            Log.d(tag, "ReceivedBody ${response.body()}")
                            callback(orders)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[getIssuedOrders] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<List<OrderDTO>>, t: Throwable) {
                    Log.e(tag, "[getIssuedOrders] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[getIssuedOrders] Unexpected error", e)
            callback(null)
        }
    }

    fun addOrder(orderData: OrderDTO, callback: (OrderDTO?) -> Unit) {
        Log.d(tag, "[addOrder] Starting with data: ${orderData.toString().take(200)}...")

        try {
            val call = orderRepository.addOrder(orderData)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<OrderDTO> {
                override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                    Log.d(tag, "[addOrder] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            val order = response.body()
                            Log.d(tag, "Order created with ID: ${order?.id}")
                            callback(order)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(null)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[addOrder] Failed. Code: ${response.code()}, Error: $error")
                            callback(null)
                        }
                    }
                }

                override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                    Log.e(tag, "[addOrder] Network error", t)
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[addOrder] Unexpected error", e)
            callback(null)
        }
    }

    fun updateOrderStatus(orderId: Int, orderStatus: String, callback: (OrderDTO?) -> Unit) {
        Log.d(tag, "⚡[updateOrderStatus] Starting for order ID: $orderId, status: $orderStatus")

        OrderStatusService().getOrderStatus(orderStatus) { orderStatusDTO ->
            if (orderStatusDTO != null) {
                Log.d(tag, "Found status DTO: ${orderStatusDTO.name}")

                try {
                    val call = orderRepository.updateOrderStatus(orderId, orderStatusDTO)
                    Log.d(tag, "Request URL: ${call.request().url}")

                    call.enqueue(object : Callback<OrderDTO> {
                        override fun onResponse(call: Call<OrderDTO>, response: Response<OrderDTO>) {
                            Log.d(tag, "[updateOrderStatus] Response code: ${response.code()}")

                            when {
                                response.isSuccessful -> {
                                    val updatedOrder = response.body()
                                    Log.d(tag, "Order $orderId updated to status: $orderStatus")
                                    callback(updatedOrder)
                                }
                                response.code() == 401 -> {
                                    Log.w(tag, "Unauthorized access - token might be expired")
                                    callback(null)
                                }
                                else -> {
                                    val error = response.errorBody()?.string() ?: "no error body"
                                    Log.e(tag, "[updateOrderStatus] Failed. Code: ${response.code()}, Error: $error")
                                    callback(null)
                                }
                            }
                        }

                        override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                            Log.e(tag, "[updateOrderStatus] Network error", t)
                            callback(null)
                        }
                    })
                } catch (e: Exception) {
                    Log.e(tag, "[updateOrderStatus] Unexpected error", e)
                    callback(null)
                }
            } else {
                Log.e(tag, "[updateOrderStatus] Status '$orderStatus' not found")
                callback(null)
            }
        }
    }

    fun deleteByID(id: Int, callback: (Boolean?) -> Unit) {
        Log.d(tag, "[deleteByID] Starting for order ID: $id")

        try {
            val call = orderRepository.deleteByID(id)
            Log.d(tag, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d(tag, "[deleteByID] Response code: ${response.code()}")

                    when {
                        response.isSuccessful -> {
                            Log.d(tag, "Order $id deleted successfully")
                            callback(true)
                        }
                        response.code() == 401 -> {
                            Log.w(tag, "Unauthorized access - token might be expired")
                            callback(false)
                        }
                        else -> {
                            val error = response.errorBody()?.string() ?: "no error body"
                            Log.e(tag, "[deleteByID] Failed. Code: ${response.code()}, Error: $error")
                            callback(false)
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(tag, "[deleteByID] Network error", t)
                    callback(false)
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "[deleteByID] Unexpected error", e)
            callback(false)
        }
    }

}