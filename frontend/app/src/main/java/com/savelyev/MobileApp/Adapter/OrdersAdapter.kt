package com.savelyev.MobileApp.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.savelyev.MobileApp.Fragment.OrderHistoryFragment
import com.savelyev.MobileApp.Api.DTO.Enum.StatusEnum
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.Fragment.OrderFragment
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.AlertUserManager
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.TimeManager
import com.savelyev.MobileApp.Utils.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersAdapter(
    private val fragment: Fragment,
    private val bindingInterface: OrderBindingInterface? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TAG = "OrdersAdapter"
    }

    private var mOrdersList: ArrayList<OrderDTO> = ArrayList()
    private var filteredOrders = mutableListOf<OrderDTO>()
    private var allowedStatuses: Set<String>? = null
    private var totalPriceOrder: Int = 0

    interface OrderBindingInterface {
        fun updateTotalPrice(total: Int)
        fun showEmptyState(show: Boolean)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.order_card, parent, false)
        return OrdersViewHolder(itemView, BikesService())
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is OrdersViewHolder) {
            val order = filteredOrders[position]
            holder.bind(mOrder = order)
            val confirmReturnButton = holder.itemView.findViewById<MaterialButton>(R.id.confirmReturnButton)
            val deleteOrderButton = holder.itemView.findViewById<ImageButton>(R.id.deleteOrderButton)
            val cardStatusLinear = holder.itemView.findViewById<LinearLayout>(R.id.card_status)
            val statusOrderText = holder.itemView.findViewById<TextView>(R.id.status_order_text)

            holder.itemView.setOnClickListener {
                if (fragment is OrderFragment && UserManager.isAdminOrManager()) {
                    fragment.showOrderDetails(order)
                }
            }
            val statusColor = when (order.status) {
//                "NEW" -> R.color.light_blue
//                "ISSUED" -> R.color.light_blue
//                "AWAITING_PAYMENT" -> R.color.light_blue
//                "PAID" -> R.color.light_blue
//                "AWAITING_CONFIRMATION" -> R.color.light_blue
                "COMPLETED" -> R.color.light_grey
                "CANCELLED" -> R.color.light_grey
                else -> R.color.light_blue
            }

            cardStatusLinear.apply{
                visibility = if (
                    fragment is OrderHistoryFragment
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), statusColor))
            }

            statusOrderText.apply {
               text = StatusEnum.fromStatus(order.status)
            }

            confirmReturnButton.apply {
                visibility = if (
                    UserManager.isAdminOrManager() &&
                    order.status == StatusEnum.AWAITING_CONFIRMATION.name
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            confirmReturnButton.setOnClickListener {
                confirmOrder(order.id)
            }

            deleteOrderButton.apply {
                visibility = if (
                    UserManager.isAdminOrManager() ||
                    fragment is OrderHistoryFragment
                ){
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
            deleteOrderButton.setOnClickListener {
                AlertUserManager().showDeleteConfirmationDialog(
                    fragment.requireContext(),
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

    fun clearLists() {
        if (mOrdersList.isEmpty()) mOrdersList.clear()
        if (filteredOrders.isEmpty()) filteredOrders.clear()
        setupOrders(null)
    }

    private fun checkEmptyState() {
        val isEmpty = filteredOrders.isEmpty()
        Log.d("ORDER_DEBUG", "Empty state: $isEmpty")
        bindingInterface?.showEmptyState(isEmpty)
    }

    fun updateOrderStatus(status: StatusEnum, callback: (successCount: Int) -> Unit = { _ -> }) {
        var successCount = 0
        val newStatus = status.name
        filteredOrders.forEach { order ->
            OrderService().updateOrderStatus(order.id, newStatus) { updatedOrder ->
                updatedOrder?.let {
                    activity?.runOnUiThread {
                        updateFilteredOrders(it)
                        if (it.status == status.name) successCount++

                        if (successCount == filteredOrders.size) {
                            applyFilter()
                            callback(successCount)
                        }
                    }
                } ?: run {
                    activity?.runOnUiThread {
                        callback(0)
                    }
                }
            }
        }
    }

    private fun confirmOrder(orderId: Int) {
        OrderService().updateOrderStatus(orderId, StatusEnum.COMPLETED.name) { updatedOrder ->
            activity?.runOnUiThread {
                updatedOrder?.let {
                    // Обновляем данные в списках
                    val mainListIndex = mOrdersList.indexOfFirst { it.id == orderId }
                    if (mainListIndex != -1) mOrdersList[mainListIndex] = updatedOrder

                    val filteredIndex = filteredOrders.indexOfFirst { it.id == orderId }
                    if (filteredIndex != -1) {
                        filteredOrders[filteredIndex] = updatedOrder
                        notifyItemChanged(filteredIndex)
                    }
                    applyFilter()
                    PushManager.showToast("Статус аренды обновлен")
                } ?: run {
                    PushManager.showToast("Ошибка обновления статуса")
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

    fun filterByStatuses(statuses: Set<StatusEnum>? = null) {
        allowedStatuses = statuses?.map { it.name }?.toSet()
        applyFilter()
    }

    fun filterByStatus(status: StatusEnum?) {
        filterByStatuses(status?.let { setOf(it) })
    }

    private fun applyFilter() {
        Log.d("ORDER_DEBUG", "Applying filter: $allowedStatuses")
        filteredOrders = if (allowedStatuses == null) {
            mOrdersList.toMutableList()
        } else {
            mOrdersList.filter { order ->
                allowedStatuses!!.contains(order.status)
            }.toMutableList()
        }
        Log.d("ORDER_DEBUG", "Filter result: ${filteredOrders.size} items")
        updateAfterFilter()
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
        bindingInterface?.updateTotalPrice(totalPriceOrder)
        bindingInterface?.showEmptyState(filteredOrders.isEmpty())
    }

    private fun removeItem(position: Int) {
        if (position in filteredOrders.indices) {
            val order = filteredOrders[position]
            mOrdersList.removeAll { it.id == order.id }

            filteredOrders.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, filteredOrders.size)

            totalPriceOrder = filteredOrders.sumOf { it.price }
            bindingInterface?.updateTotalPrice(totalPriceOrder)
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
