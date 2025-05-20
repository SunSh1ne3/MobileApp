package com.savelyev.MobileApp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.savelyev.MobileApp.Adapter.OrdersAdapter
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.UserManager

class OrderHistoryFragment : Fragment() {
    private val orderService = OrderService()

    private lateinit var orderAdapter: OrdersAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_order_history, container, false)
        progressBar = root.findViewById(R.id.progress_bar)
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        recyclerView = root.findViewById(R.id.recyclerOrderView)

        orderAdapter = OrdersAdapter(this)
        recyclerView.adapter = orderAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchOrders()
        swipeRefreshLayout.setOnRefreshListener {
            fetchOrders()
        }
        return root
    }

    private fun fetchOrders() {
        setLoading(true)
        val userId = UserManager.getCurrentUser()?.id
        if (userId != null) {
            orderService.getUserOrderList(userId) { ordersList ->
                activity?.runOnUiThread {
                    setLoading(false)
                    swipeRefreshLayout.isRefreshing = false

                    when {
                        !ordersList.isNullOrEmpty() -> {
                            orderAdapter.setupOrders(ordersList)
                        }

                        ordersList != null && ordersList.isEmpty() -> {
                            PushManager.showToast("История пуста")
                        }

                        else -> {
                            PushManager.showToast("Не удалось загрузить историю поездок")
                        }
                    }
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

}