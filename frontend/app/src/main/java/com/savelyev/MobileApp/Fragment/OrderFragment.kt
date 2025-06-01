package com.savelyev.MobileApp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.savelyev.MobileApp.Activity.ContentActivity
import com.savelyev.MobileApp.Adapter.OrdersAdapter
import com.savelyev.MobileApp.Api.DTO.Enum.StatusEnum
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.QrData
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.AlertUserManager
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.UserManager

class OrderFragment : Fragment(), OrdersAdapter.OrderBindingInterface {
    private val orderService = OrderService()
    private var qrResultListener: () -> Unit = {}
    private lateinit var orderAdapter: OrdersAdapter

    private lateinit var filterLayout: LinearLayout
    private lateinit var btnNewOrders: MaterialButton
    private lateinit var btnActiveOrders: MaterialButton

    private lateinit var totalPriceTextView: TextView
    private lateinit var totalPriceTitleView: TextView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var bottomLayout: LinearLayout
    private lateinit var payButton: MaterialButton
    private lateinit var issueButton: MaterialButton
    private lateinit var goToCatalog: MaterialButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_order, container, false)
        initComponents(root)
        setupListeners()
        setupQrResultListener()
        setupFilter()
        if (!UserManager.isAdminOrManager()) {
            fetchOrders()
            orderAdapter.filterByStatus(StatusEnum.NEW)
        }


        return root
    }

    private fun initComponents(root: View) {
        filterLayout =  root.findViewById(R.id.filter_layout)
        btnNewOrders =  root.findViewById(R.id.btn_new_orders)
        btnActiveOrders =  root.findViewById(R.id.btn_active_orders)

        totalPriceTextView = root.findViewById(R.id.total_price)
        totalPriceTitleView = root.findViewById(R.id.total_price_text)
        emptyStateLayout = root.findViewById(R.id.empty_state_layout)
        progressBar = root.findViewById(R.id.progress_bar)
        bottomLayout = root.findViewById(R.id.bottom_layout)
        payButton = root.findViewById(R.id.payButton)
        issueButton = root.findViewById(R.id.issueButton)
        goToCatalog = root.findViewById(R.id.empty_state_button)

        orderAdapter = OrdersAdapter(this, this)

        recyclerView = root.findViewById(R.id.recyclerOrderView)
        recyclerView.adapter = orderAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        btnNewOrders.setOnClickListener {
            switchFilter(isActiveOrders = false)
        }
        btnActiveOrders.setOnClickListener {
            switchFilter(isActiveOrders = true)
            fetchIssuedOrders()
        }

        payButton.setOnClickListener {
            AlertUserManager().showPaymentDialog(
                fragment = this,
                onCardPayment = { startCardPayment() },
                onCashPayment = { confirmCashPayment() }
            )
        }

        issueButton.setOnClickListener {
            showIssueConfirmationDialog()
        }

        goToCatalog.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_orderFragment_to_listFragment)
        }

        (requireActivity() as ContentActivity).setScanQRButtonListener {
            findNavController().navigate(R.id.action_orderFragment_to_qrScannerFragment)
        }
    }

    private fun setupFilter() {
        if (UserManager.isAdminOrManager()) {
            filterLayout.visibility = View.VISIBLE
            btnNewOrders.isSelected = true
            btnActiveOrders.isSelected = false
            switchFilter(isActiveOrders = false)
        }
        else {
            filterLayout.visibility = View.GONE
        }
    }

    private fun setupQrResultListener() {
        parentFragmentManager.clearFragmentResultListener(QrScannerFragment.QR_SCAN_RESULT)

        qrResultListener = {
            Log.d("QR_DEBUG", "Launching QR scanner")
            parentFragmentManager.setFragmentResultListener(
                QrScannerFragment.QR_SCAN_RESULT,
                viewLifecycleOwner
            ) { requestKey, bundle ->
                Log.d("QR_DEBUG", "Received fragment result with key: $requestKey")
                if (requestKey == QrScannerFragment.QR_SCAN_RESULT) {
                    val qrData = bundle.getParcelable<QrData>("qr_data")
                    if (qrData != null) {
                        Log.d("QR_DEBUG", "Processing QR data in OrderFragment: $qrData")
                        handleQrData(qrData)
                    } else {
                        Log.e("QR_DEBUG", "Received null QR data in OrderFragment")
                        PushManager.showToast("Не удалось прочитать QR-код")
                    }
                }
            }
        }
        qrResultListener.invoke()
    }

    private fun fetchOrders() {
        setLoading(true)
        val userId = UserManager.getCurrentUser()?.id
        if (userId != null) {
            orderService.getUserOrderList(userId) { ordersList ->
                activity?.runOnUiThread {
                    setLoading(false)
                    orderAdapter.setupOrders(ordersList)
                }
            }
        }
        setLoading(false)
    }

    private fun fetchIssuedOrders() {
        setLoading(true)
        orderService.getIssuedOrders { ordersList ->
            activity?.runOnUiThread {
                setLoading(false)
                orderAdapter.setupOrders(ordersList)
            }
        }
        setLoading(false)
    }

    private fun startCardPayment() {
        AlertUserManager().showComingSoonDialog(this)
        //TODO("Возможно будет доступна, надо подключать, пока заглушка")
    }

    private fun confirmCashPayment() {
        AlertUserManager().showConfirmationDialog(
            this,
            title = this.getString(R.string.CASH_PAYMENT_TITLE),
            message = this.getString(R.string.CASH_PAYMENT_INFO_MESSAGE),
        ) {
            setLoading(true)
            orderAdapter.updateOrderStatus(StatusEnum.AWAITING_PAYMENT) { successCount ->
                setLoading(false)
                Log.d("OrderUpdate", "Обновлено $successCount заказов")
                if (successCount > 0) {
                    orderAdapter.filterByStatus(StatusEnum.NEW)
                    PushManager.showToast("Статус заказа обновлен")
                } else {
                    PushManager.showToast("Не удалось обновить статус заказа")
                }
            }
        }
    }

    private fun showIssueConfirmationDialog() {
        AlertUserManager().showConfirmationDialog(
            this,
            title = "Подтверждение выдачи",
            message = """
                1. Проверьте оплату
                2. Убедитесь, что велосипеды готовы к выдаче
            """.trimIndent(),
        ) {
            setLoading(true)
            orderAdapter.updateOrderStatus(StatusEnum.ISSUED) { successCount ->
                setLoading(false)
                Log.d("OrderUpdate", "Обновлено $successCount заказов")
                if (successCount > 0) {
                    PushManager.showToast("Заказы переведены в статус 'Выдано'")
                    fetchOrders()
                } else {
                    PushManager.showToast("Не удалось обновить статус заказа")
                }
            }
        }
    }

    private fun handleQrData(qrData: QrData) {
        Log.d("QR_DATA", "Received QR data: $qrData")
        PushManager.showToast("Получены данные: $qrData", context = this.requireContext())

        setLoading(true)
        orderService.getUserActiveOrders(qrData.userId) { orders ->
            activity?.runOnUiThread {
                setLoading(false)
                if (!orders.isNullOrEmpty()) {
                    orderAdapter.setupOrders(orders)
                    orderAdapter.filterByStatuses(setOf(
                        StatusEnum.PAID,
                        StatusEnum.AWAITING_PAYMENT
                    ))
                } else {
                    PushManager.showToast("Не найдено заказов по указанным ID")
                    showEmptyState(true)
                }
            }
        }
    }


    private fun switchFilter(isActiveOrders: Boolean) {
        btnNewOrders.isSelected = !isActiveOrders
        btnActiveOrders.isSelected = isActiveOrders

        btnNewOrders.background = getDrawable(
            requireContext(),
            R.drawable.filter_button_background
        )
        btnActiveOrders.background = getDrawable(
            requireContext(),
            R.drawable.filter_button_background
        )

        if (isActiveOrders) {
            fetchIssuedOrders()
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            bottomLayout.visibility = View.GONE
        } else {
            orderAdapter.clearLists()
        }

        (requireActivity() as ContentActivity)
            .findViewById<ImageView>(R.id.scan_qr_button).visibility =
            if (isActiveOrders) View.GONE else View.VISIBLE

    }

    @SuppressLint("SetTextI18n")
    override fun updateTotalPrice(total: Int) {
        val isManager = UserManager.isAdminOrManager()
        val orders = orderAdapter.getCurrentOrders()
        val allPaid = orders.all { it.status == StatusEnum.PAID.name }

        when {
            isManager && allPaid -> {
                totalPriceTitleView.text = StatusEnum.PAID.name
                totalPriceTextView.visibility = View.GONE
                payButton.visibility = View.GONE
                issueButton.visibility = View.VISIBLE
            }
            isManager -> {
                totalPriceTitleView.text = getString(R.string.ORDER_TOTAL_PRICE_TEXT)
                totalPriceTextView.text = "$total р"
                totalPriceTextView.visibility = View.VISIBLE
                payButton.visibility = View.GONE
                issueButton.visibility = View.VISIBLE
            }
            else -> {
                totalPriceTitleView.text = getString(R.string.ORDER_TOTAL_PRICE_TEXT)
                totalPriceTextView.text = "$total р"
                totalPriceTextView.visibility = View.VISIBLE
                payButton.visibility = if (total > 0) View.VISIBLE else View.GONE
                issueButton.visibility = View.GONE
            }
        }

        bottomLayout.visibility = if (total > 0 || (isManager && allPaid && !btnActiveOrders.isSelected)) View.VISIBLE else View.GONE
    }


    override fun showEmptyState(show: Boolean) {
        if (progressBar.visibility == View.GONE) {
            emptyStateLayout.visibility = if (show && !UserManager.isAdminOrManager()) View.VISIBLE else View.GONE
            bottomLayout.visibility = if (show || btnActiveOrders.isSelected) View.GONE else View.VISIBLE
            recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        emptyStateLayout.visibility = View.GONE
    }

    fun showOrderDetails(order: OrderDTO) {
        OrderDetailsBottomSheet.newInstance(order).show(parentFragmentManager, "OrderDetails")
    }

    override fun onResume() {
        super.onResume()
        qrResultListener.invoke()
    }

    override fun onPause() {
        super.onPause()
        parentFragmentManager.clearFragmentResultListener(QrScannerFragment.QR_SCAN_RESULT)
    }
}