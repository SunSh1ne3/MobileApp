package com.savelyev.MobileApp.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.savelyev.MobileApp.Activity.OrderFragment
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.Api.Service.OrderStatusService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.AlertUserManager
import com.savelyev.MobileApp.Utils.TimeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersAdapter(
    private val context: OrderFragment,
    private val bindingInterface: OrderBindingInterface) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG = "OrdersAdapter"
    }

    private var mOrdersList: ArrayList<OrderDTO> = ArrayList()
    private var filteredOrders = mutableListOf<OrderDTO>()
    private var currentFilter: String? = null
    private var totalPriceOrder: Int = 0

    interface OrderBindingInterface {
        fun updateTotalPrice(total: Int)
        fun showEmptyState(show: Boolean)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.order_card, parent, false)
        return OrdersViewHolder(itemView, BikesService())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrdersViewHolder) {
            val order = filteredOrders[position]
            holder.bind(mOrder = order)
            holder.itemView.findViewById<ImageButton>(R.id.deleteOrderButton).setOnClickListener {
                AlertUserManager().showDeleteConfirmationDialog(
                    context.requireContext(),
                    "Отмена заказа",
                    "Точно хотите отменить аренду велосипеда?"
                ) {
                    performDelete(order.id, position)
                }
            }
        }
    }

    override fun getItemCount(): Int = filteredOrders.size
    fun getCurrentOrders(): List<OrderDTO> = filteredOrders.toList()



    fun setupOrders(ordersList: List<OrderDTO>?, callback: (() -> Unit)? = null) {
        mOrdersList.clear()
        ordersList?.let { mOrdersList.addAll(it)}
        applyFilter()
        callback?.invoke()
    }


    private fun checkEmptyState() {
        bindingInterface.showEmptyState(filteredOrders.isEmpty())
    }

    fun updateOrderStatus(newStatus: String, callback: (successCount: Int) -> Unit = { _ -> }) {
        OrderStatusService().getStatus(newStatus) { statusDTO ->
            if (statusDTO == null) {
                callback(0)
                return@getStatus
            }

            var successCount = 0

            filteredOrders.forEach { order ->
                OrderService().updateOrderStatus(order.id, statusDTO.name) { updatedOrder ->
                    updatedOrder?.let {
                        activity?.runOnUiThread {
                            updateFilteredOrders(it)
                            if (it.status == statusDTO.id) {
                                successCount++
                            }

                            // Если это последнее обновление
                            if (successCount == filteredOrders.size) {
                                applyFilter() // Переприменяем фильтр
                                callback(successCount)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateFilteredOrders(updatedOrder: OrderDTO) {
        // 1. Обновляем основной список
        val indexInMainList = mOrdersList.indexOfFirst { it.id == updatedOrder.id }
        if (indexInMainList != -1) {
            mOrdersList[indexInMainList] = updatedOrder
        }

        // 2. Обновляем отфильтрованный список
        val indexInFilteredList = filteredOrders.indexOfFirst { it.id == updatedOrder.id }
        if (indexInFilteredList != -1) {
            filteredOrders[indexInFilteredList] = updatedOrder
            notifyItemChanged(indexInFilteredList)
        }
    }

    fun filterByStatus(status: String?) {
        currentFilter = status
        applyFilter()
    }


    private fun applyFilter() {
        if (currentFilter.isNullOrEmpty()) {
            filteredOrders = mOrdersList.toMutableList()
            updateAfterFilter()
            return
        }

        if (mOrdersList.isEmpty()) {
            filteredOrders.clear()
            updateAfterFilter()
            return
        }

        OrderStatusService().getStatus(currentFilter!!) { status ->
            activity?.runOnUiThread {
                filteredOrders = if (status != null) {
                    mOrdersList.filter { it.status == status.id }.toMutableList()
                } else {
                    mOrdersList.toMutableList()
                }
                updateAfterFilter()
            }
        }
    }

    private fun updateAfterFilter() {
        calculateTotalSum()
        checkEmptyState()
        notifyDataSetChanged()
    }

    private fun performDelete(idOrder: Int, position: Int) {
        if (position !in filteredOrders.indices) return

        OrderService().deleteByID(idOrder) { isSuccess ->
            activity?.runOnUiThread {
                if (isSuccess == true) {
                    val order = filteredOrders[position]
                    mOrdersList.removeAll { it.id == order.id }
                    removeItem(position)
                } else {
                    Log.e(TAG, "Failed to delete order $idOrder")
                }
            }
        }
    }

    private fun calculateTotalSum() {
        totalPriceOrder = filteredOrders.sumOf { it.price }
        updateTotalPrice()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        bindingInterface.updateTotalPrice(totalPriceOrder)
        bindingInterface.showEmptyState(filteredOrders.isEmpty())
    }

    private fun removeItem(position: Int) {
        if (position in filteredOrders.indices) {
            val order = filteredOrders[position]
            mOrdersList.removeAll { it.id == order.id }

            filteredOrders.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, filteredOrders.size)

            totalPriceOrder = filteredOrders.sumOf { it.price }
            bindingInterface.updateTotalPrice(totalPriceOrder)
            checkEmptyState()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    class OrdersViewHolder(itemView: View, private val bikesService: BikesService) :
        RecyclerView.ViewHolder(itemView) {
        private val dateStartTextView: TextView = itemView.findViewById(R.id.date_start_order)
        private val dateEndTextView: TextView = itemView.findViewById(R.id.date_end_order)
        private val durationTextView: TextView = itemView.findViewById(R.id.duration)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val nameBicycleTextView: TextView = itemView.findViewById(R.id.name_bicycle_text)

        @SuppressLint("SetTextI18n")
        fun bind(mOrder: OrderDTO) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bikeDTO = withContext(Dispatchers.IO) {
                        bikesService.getBikeDTO(mOrder.bicycleId)
                    }

                    nameBicycleTextView.text = bikeDTO?.name ?: "Неизвестный велосипед"
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading bike: ${e.message}")
                    nameBicycleTextView.text = "Ошибка загрузки"
                }
            }

            dateStartTextView.text = TimeManager().getWithoutYearFormatZoneTime(mOrder.startDate)
            dateEndTextView.text = TimeManager().getWithoutYearFormatZoneTime(mOrder.endDate)
            durationTextView.text = formatDuration(mOrder.countHours, mOrder.countDays)
            priceTextView.text = "${mOrder.price} р"
        }

        private fun formatDuration(countHours: Int?, countDays: Int?): String {
            return when {
                countDays!! > 0 && countHours!! > 0 -> "$countDays д $countHours ч"
                countDays > 0 -> "$countDays д"
                else -> "$countHours ч"
            }
        }
    }


    private val activity: Activity?
        get() = (bindingInterface as? Fragment)?.activity
}
