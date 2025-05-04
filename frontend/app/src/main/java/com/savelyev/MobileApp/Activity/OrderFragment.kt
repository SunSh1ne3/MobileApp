package com.savelyev.MobileApp.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.savelyev.MobileApp.Adapter.OrdersAdapter
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.Api.Service.UserService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.AlertUserManager

class OrderFragment : Fragment(), OrdersAdapter.OrderBindingInterface {
    private val orderService = OrderService()
    private lateinit var orderAdapter: OrdersAdapter

    private lateinit var totalPriceTextView: TextView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var bottomLayout: LinearLayout
    private lateinit var payBottom: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_order, container, false)

        recyclerView = root.findViewById(R.id.recyclerOrderView)
        totalPriceTextView = root.findViewById(R.id.total_price)
        emptyStateLayout = root.findViewById(R.id.empty_state_layout)
        progressBar = root.findViewById(R.id.progress_bar)
        bottomLayout = root.findViewById(R.id.bottom_layout)
        payBottom = root.findViewById(R.id.addButton)

        val goToCatalog = root.findViewById<MaterialButton>(R.id.empty_state_button)
        orderAdapter = OrdersAdapter(this, this)
        orderAdapter.filterByStatus("Новый")
        recyclerView.adapter = orderAdapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchOrders()

        payBottom.setOnClickListener {
            AlertUserManager().showPaymentDialog(
                fragment = this,
                onCardPayment = { startCardPayment() },
                onCashPayment = { confirmCashPayment() }
            )
        }
        goToCatalog.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_orderFragment_to_listFragment)
        }

        return root
    }

    private fun startCardPayment() {
        AlertUserManager().showComingSoonDialog(this)
        //TODO("Возможно будет доступна, надо подключать, пока заглушка")


    }

    private fun confirmCashPayment() {
        AlertUserManager().showCashPaymentConfirmation(this) {
            setLoading(true)
            orderAdapter.updateOrderStatus("Ожидает оплаты") { successCount ->
                setLoading(false)
                Log.d("OrderUpdate", "Обновлено $successCount заказов")
                if (successCount > 0) {
                    orderAdapter.filterByStatus("Новый")
                    Toast.makeText(
                        requireContext(),
                        "Статус заказа обновлен",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось обновить статус заказа",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    private fun fetchOrders() {
        setLoading(true)
        val userId = UserService(requireContext()).extractUserIdFromToken()
        orderService.getUserOrderList(userId) { ordersList ->
            activity?.runOnUiThread {
                setLoading(false)
                orderAdapter.setupOrders(ordersList)
            }
        }
    }

    private fun ShowToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    override fun updateTotalPrice(total: Int) {
        totalPriceTextView.text = "$total р"
        bottomLayout.visibility = if (total > 0) View.VISIBLE else View.GONE
    }

    override fun showEmptyState(show: Boolean) {
        emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        bottomLayout.visibility = if (show) View.GONE else View.VISIBLE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (loading) View.GONE else View.VISIBLE
        payBottom.isEnabled = !loading
    }



}