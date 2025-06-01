package com.savelyev.MobileApp.Fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.savelyev.MobileApp.Utils.TimeManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.savelyev.MobileApp.Activity.ContentActivity
import com.savelyev.MobileApp.Adapter.BikesAdapter
import com.savelyev.MobileApp.Api.DTO.Enum.StatusEnum
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.AlertUserManager
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.UserManager

class ListFragment : Fragment() {

    private val bikeAdapter = BikesAdapter(this)
    private val bikesService = BikesService()
    private lateinit var activeOrderCard: MaterialCardView
    private lateinit var orderSummary: TextView
    private lateinit var orderCardTitle: TextView
    private lateinit var qrButton: ImageButton
    private lateinit var returnButton: MaterialButton

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    private var activeOrders: List<OrderDTO>? = null
    private var issuedOrders: List<OrderDTO>? = null
    private var awaitConfirmOrders: List<OrderDTO>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        initComponents(root)
        setupListeners()
        setupPage()

        return root
    }

    private fun initComponents(root: View) {
        activeOrderCard = root.findViewById(R.id.active_order_card)
        qrButton = root.findViewById(R.id.qr_button)
        returnButton = root.findViewById(R.id.return_button)

        progressBar = root.findViewById(R.id.progress_bar)
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)

        orderSummary = root.findViewById(R.id.order_summary)
        orderCardTitle = root.findViewById(R.id.active_orders_title)

        recyclerView = root.findViewById(R.id.recyclerView)

        recyclerView.adapter = bikeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            setupPage()
        }

        returnButton.setOnClickListener {
            AlertUserManager().showConfirmationDialog(
                this,
                title = "Ожидайте подтверждения",
                message = "Ваш запрос на возврат велосипеда передан менеджеру. " +
                        "Статус аренды обновится после проверки.",
            ) {
                updateOrderStatus(StatusEnum.AWAITING_CONFIRMATION) { successCount ->
                    Log.d("OrderUpdate", "Обновлено $successCount заказов")
                    if (successCount > 0) {
                        updateOrders()
                        PushManager.showToast("Статус заказа обновлен")
                    } else {
                        PushManager.showToast("Не удалось обновить статус заказа")
                    }
                }

            }
        }

        qrButton.setOnClickListener {
            if (!activeOrders.isNullOrEmpty()) {
                AlertUserManager().showCompactQrDialog(this, activeOrders!!)
            } else {
                PushManager.showToast("Нет активных заказов")
            }
        }

        (requireActivity() as ContentActivity).setAddBicycleButtonListener {
            findNavController().navigate(R.id.action_listFragment_to_addBicycleFragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateOrderStatus(
        status: StatusEnum,
        orders: List<OrderDTO> = issuedOrders ?: emptyList(),
        callback: (successCount: Int) -> Unit
    ) {
        var successCount = 0
        orders.forEach { order ->
            OrderService().updateOrderStatus(order.id, status.name) { updatedOrder ->
                updatedOrder?.let {
                    // Обновляем локальные данные
                    issuedOrders = issuedOrders?.map { if (it.id == order.id) updatedOrder else it }
                    activeOrders = activeOrders?.map { if (it.id == order.id) updatedOrder else it }

                    if (it.status == status.name) successCount++

                    if (successCount == orders.size) {
                        loadActiveOrders()
                        loadIssueOrders()
                        updateActiveOrdersInfo()
                        callback(successCount)
                    }
                } ?: run {
                    callback(0)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPage()
    {
        fetchBikes()
        updateOrders()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateOrders()
    {
        loadActiveOrders()
        loadIssueOrders()
        loadAwaitingConfirmOrders()
        updateActiveOrdersInfo()
    }


    private fun fetchBikes() {
        progressBar.visibility = View.VISIBLE
        bikesService.getBikeList { bikesList ->
            activity?.runOnUiThread {
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false

                when {
                    !bikesList.isNullOrEmpty() -> {
                        bikeAdapter.setupBikes(bikesList)
                    }

                    bikesList != null && bikesList.isEmpty() -> {
                        PushManager.showToast("Список велосипедов пуст")
                    }

                    else -> {
                        PushManager.showToast("Не удалось загрузить список велосипедов")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadActiveOrders() {
        if (!UserManager.isInitialized()) {
            UserManager.init(requireContext())
        }
        val userId = UserManager.getCurrentUser()?.id ?: return
        OrderService().getUserActiveOrders(userId) { orderList ->
            activity?.runOnUiThread {
               when{
                   orderList == null -> {
                       activeOrderCard.visibility = View.GONE
                       PushManager.showToast("Ошибка загрузки")
                   }
                   orderList.isEmpty() -> {
                       // Нет активных заказов
                       activeOrderCard.visibility = View.GONE
                       activeOrders = null
                   }
                   else -> {
                       activeOrders = orderList
                       // Есть активные заказы
                       activeOrderCard.visibility = View.VISIBLE
                       updateActiveOrdersInfo()
                   }
               }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadIssueOrders() {
        if (!UserManager.isInitialized()) {
            UserManager.init(requireContext())
        }
        val userId = UserManager.getCurrentUser()?.id ?: return
        OrderService().getUserIssuedOrders(userId) { orderList ->
            activity?.runOnUiThread {
                when{
                    orderList == null -> {
                        activeOrderCard.visibility = View.GONE
                        PushManager.showToast("Ошибка загрузки")
                    }
                    orderList.isEmpty() -> {
                        // Нет выданных заказов
                        activeOrderCard.visibility = View.GONE
                        issuedOrders = null
                    }
                    else -> {
                        issuedOrders = orderList
                        // Есть выданные заказы
                        activeOrderCard.visibility = View.VISIBLE
                        updateActiveOrdersInfo()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAwaitingConfirmOrders() {
        if (!UserManager.isInitialized()) {
            UserManager.init(requireContext())
        }
        val userId = UserManager.getCurrentUser()?.id ?: return
        OrderService().getUserAwaitingConfirmOrders(userId) { orderList ->
            activity?.runOnUiThread {
                when{
                    orderList == null -> {
                        activeOrderCard.visibility = View.GONE
                        PushManager.showToast("Ошибка загрузки")
                    }
                    orderList.isEmpty() -> {
                        // Нет заказов для подтверждения
                        activeOrderCard.visibility = View.GONE
                        awaitConfirmOrders = null
                    }
                    else -> {
                        awaitConfirmOrders = orderList
                        // Есть заказы для подтверждения
                        activeOrderCard.visibility = View.VISIBLE
                        updateActiveOrdersInfo()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun updateActiveOrdersInfo() {

        if (!activeOrders.isNullOrEmpty()) {
            orderCardTitle.text = getString(R.string.COMING_ORDER_TITLE)
            orderSummary.text = buildString {
                append("${activeOrders?.size} ")
                append(activeOrders?.size?.let { pluralForm( it ) })
                append(" · Начало: ${TimeManager().formatStartDateForDisplay(activeOrders!!)}")
            }
            qrButton.visibility = View.VISIBLE
            returnButton.visibility = View.GONE
        }

        if (!issuedOrders.isNullOrEmpty()) {
            orderCardTitle.text = getString(R.string.ACTIVE_ORDER_TITLE)
            orderSummary.text = buildString {
                append("${issuedOrders?.size} ")
                append(issuedOrders?.size?.let { pluralForm( it ) })
                append(" · Конец: ${TimeManager().formatEndDateForDisplay(issuedOrders!!)}")
            }
            qrButton.visibility = View.GONE
            returnButton.visibility = View.VISIBLE
        }

        if (!awaitConfirmOrders.isNullOrEmpty()) {
            orderCardTitle.text = getString(R.string.ACTIVE_ORDER_TITLE)
            orderSummary.text ="Возврат ожидает подтверждения менеджера"
            qrButton.visibility = View.GONE
            returnButton.visibility = View.GONE
        }
    }

    private fun pluralForm(
        n: Int,
        form1: String = "велосипед",
        form2: String = "велосипеда",
        form5: String = "велосипедов"
    ): String {
        return when {
            n % 10 == 1 && n % 100 != 11 -> form1
            n % 10 in 2..4 && n % 100 !in 12..14 -> form2
            else -> form5
        }
    }
}
