package com.savelyev.MobileApp.Fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.UserDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.Api.Service.UserService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.TimeManager

class OrderDetailsBottomSheet : BottomSheetDialogFragment() {
    private val userService = UserService()
    private val bikesService = BikesService()

    private lateinit var userName: TextView
    private lateinit var userPhone: TextView
    private lateinit var bicycleInfo: TextView
    private lateinit var timeData: TextView

    private var order: OrderDTO? = null
    private var actualUser: UserDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_details_bottom_sheet, container, false)
        order = arguments?.getParcelable("ORDER") ?: return view
        initComponents(view)
        setupListeners()
        setupData()

        return view
    }

    companion object {
        fun newInstance(order: OrderDTO) = OrderDetailsBottomSheet().apply {
            arguments = Bundle().apply { putParcelable("ORDER", order) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initComponents(view: View) {
        userName = view.findViewById(R.id.tv_user_name)
        userPhone = view.findViewById(R.id.tv_user_phone)
        bicycleInfo = view.findViewById(R.id.tv_bike_info)
        timeData = view.findViewById(R.id.tv_end_time)
    }

    private fun setupListeners() {
        userPhone.setOnClickListener {
            actualUser?.numberPhone?.let { phone ->
                copyToClipboard("Телефон клиента", phone)
                PushManager.showToast("Номер скопирован в буфер")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupData()
    {
        order?.let {
            userService.getUserById(it.userId) { user, _ ->
                actualUser = user
                userName.text = "Клиент: ${user?.username ?: "Неизвестно"}"
                userPhone.text = "Телефон: ${user?.numberPhone ?: "Телефон не указан"}"
            }
        }

        bikesService.getBikeData(order?.bicycleId) { bike ->
            bicycleInfo.text = "Велосипед: ${bike?.name ?: "Неизвестно"}"
        }

        timeData.text ="Аренда до: ${order?.let { TimeManager().getWithoutYearFormatZoneTime(it.endDate) }}"
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

}