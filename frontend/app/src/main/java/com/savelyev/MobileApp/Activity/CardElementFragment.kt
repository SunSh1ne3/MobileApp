package com.savelyev.MobileApp.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButtonToggleGroup
import com.savelyev.MobileApp.Adapter.ImageBicycleAdapter
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.Api.Service.OrderService
import com.savelyev.MobileApp.Api.Service.UserService
import com.savelyev.MobileApp.CustomObject.CustomButton
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.OffsetDateTime
import java.util.Calendar


class CardElementFragment : Fragment() {

    private val bikesService = BikesService()
    private val orderService = OrderService()

    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var selectedDateTextView: TextView
    private lateinit var selectedDateContainer: LinearLayout
    private lateinit var customHourButton: CustomButton
    private lateinit var customDayButton: CustomButton
    private lateinit var totalPriceText: TextView

    private lateinit var timeManager: TimeManager

    private var isUserInteraction = false
    private var selectedDateTime: OffsetDateTime? = null
    private var totalPrice: Int? = 0
    private var savedInstanceState: Bundle? = null

    companion object {
        private const val ARG_SELECTED_DATE = "selected_date"
        private const val ARG_SELECTED_MODE = "selected_mode"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_card_element, container, false)
        val bicycleID = requireArguments().getInt("bicycleID")

        timeManager = TimeManager()

        fetchBikeData(bicycleID, root)
        if (savedInstanceState == null) {
            selectedDateTime = arguments?.getLong(ARG_SELECTED_DATE)?.let {
                timeManager.fromEpochMilli(it)
            }
        }
        this.savedInstanceState = savedInstanceState
        // Инициализация элементов
        customHourButton = root.findViewById(R.id.custom_hour_button)
        customDayButton = root.findViewById(R.id.custom_day_button)
        toggleGroup = root.findViewById(R.id.toggleGroup)
        selectedDateTextView = root.findViewById(R.id.selectedDateTextView)
        selectedDateContainer = root.findViewById(R.id.selectedDateContainer)
        totalPriceText = root.findViewById(R.id.price)
        val addButton = root.findViewById<Button>(R.id.addButton)



        addButton.setOnClickListener {
            val userId = UserService(requireContext()).extractUserIdFromToken()
            val startDate: OffsetDateTime = selectedDateTime ?: timeManager.getZoneTime()

            val orderData = OrderDTO(
                userId = userId,
                bicycleId = bicycleID,
                startDate = startDate,
                price = customHourButton.getPrice() + customDayButton.getPrice(),
                countDays = customDayButton.getCurrentCount(),
                countHours = customHourButton.getCurrentCount(),
            )
            addOrder(orderData)
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeManager = TimeManager()
        setupToggleGroup()
        restoreState(savedInstanceState)

        setupCalculateButtons()
        calculateTotal()
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupToggleGroup() {
        var isInitialSetup = true

        // Восстанавливаем состояние
        val savedMode = savedInstanceState?.getInt("SELECTED_MODE") ?: arguments?.getInt(ARG_SELECTED_MODE, R.id.btnNow)
        val savedDate = savedInstanceState?.getLong("SELECTED_DATE") ?: arguments?.getLong(ARG_SELECTED_DATE)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isUserInteraction || !isChecked || isInitialSetup) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.btnNow -> {
                    selectedDateTime = timeManager.getZoneTime()
                    selectedDateContainer.visibility = View.GONE
                    saveStateToArguments()
                }
                R.id.btnCustom -> {
                    if (selectedDateContainer.visibility != View.VISIBLE) {
                        showDateTimePicker()
                    }
                    saveStateToArguments()
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                savedMode == R.id.btnCustom && savedDate != null -> {
                    selectedDateTime = timeManager.fromEpochMilli(savedDate)
                    toggleGroup.check(R.id.btnCustom)
                    updateDateTimeDisplay()
                }
                savedMode == R.id.btnNow -> {
                    toggleGroup.check(R.id.btnNow)
                    selectedDateTime = timeManager.getZoneTime()
                    selectedDateContainer.visibility = View.GONE
                }
                else -> {
                    toggleGroup.check(R.id.btnNow)
                    selectedDateTime = timeManager.getZoneTime()
                    selectedDateContainer.visibility = View.GONE
                }
            }
            isInitialSetup = false
            isUserInteraction = true
        }, 100)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCurrentTime(dateTime: OffsetDateTime, toleranceSeconds: Long = 60): Boolean {
        val now = timeManager.getZoneTime()
        val duration = Duration.between(dateTime, now).abs()
        return duration.seconds <= toleranceSeconds
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDateTimePicker() {
        Log.d("STATE_DEBUG", "showDateTimePicker called. Current selectedDateTime: ${selectedDateTime?.toInstant()?.toEpochMilli()}")
        val current = selectedDateTime ?: timeManager.getZoneTime()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = current.toInstant().toEpochMilli()
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        selectedDateTime = OffsetDateTime.of(
                            year, month + 1, day, hour, minute, 0, 0,
                            current.offset
                        )
                        updateDateTimeDisplay()
                        saveStateToArguments()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getLong("SELECTED_DATE")?.let { millis ->
            selectedDateTime = timeManager.fromEpochMilli(millis)
            if (toggleGroup.checkedButtonId == R.id.btnCustom) {
                updateDateTimeDisplay()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateTimeDisplay() {
        Log.d("STATE_DEBUG", "updateDateTimeDisplay called. Current selectedDateTime: ${selectedDateTime?.toInstant()?.toEpochMilli()}")
        selectedDateTime?.let {
            selectedDateTextView.text = timeManager.getWithoutYearFormatZoneTime(it)
            selectedDateContainer.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedDateTime?.let {
            outState.putLong("SELECTED_DATE", it.toInstant().toEpochMilli())
        }
        outState.putInt("SELECTED_MODE", toggleGroup.checkedButtonId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveStateToArguments() {
        arguments = (arguments ?: Bundle()).apply {
            putInt(ARG_SELECTED_MODE, toggleGroup.checkedButtonId)
            if (toggleGroup.checkedButtonId == R.id.btnCustom) {
                selectedDateTime?.let {
                    putLong(ARG_SELECTED_DATE, it.toInstant().toEpochMilli())
                }
            } else {
                remove(ARG_SELECTED_DATE)
            }
        }
    }

    private fun setupCalculateButtons() {
        customHourButton.setUnitType(CustomButton.UnitType.HOUR)
        customHourButton.setPrice(200)//TODO: Хардкор, брать из БД
        customDayButton.setUnitType(CustomButton.UnitType.DAY)
        customDayButton.setPrice(800)//TODO: Хардкор, брать из БД

        customHourButton.setOnCountChangedListener(object : CustomButton.OnCountChangedListener {
            override fun onCountChanged(totalPrice: Int) {
                calculateTotal()
            }
        })

        customDayButton.setOnCountChangedListener(object : CustomButton.OnCountChangedListener {
            override fun onCountChanged(totalPrice: Int) {
                calculateTotal()
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotal() {
        totalPrice = customHourButton.getPrice() + customDayButton.getPrice()
        totalPriceText.text = "$totalPrice р"
    }


    private fun addOrder(orderData: OrderDTO) {
        orderService.addOrder(orderData) { response ->
            if (response != null) {
                ShowToast("Заказ успешно добавлен.") //TODO: добавить попап с переходом в заказы или продолжить покупки
            } else {
                ShowToast("Не удалось добавить ваш заказ, попробуйте еще раз.")
            }
        }
    }

    private fun fetchBikeData(id: Int?, root: View) {
        bikesService.getBikeData(id) { bikeData->
            if (bikeData != null) {
                setupData(bikeData, root)
                //TODO: работа с файлами будет позже
                //TODO: все работает, но надо подключать нормально через S3
                //loadImage(bikeData,root)
            } else {
                ShowToast("Ошибка загрузки данных, повторите попытку.")
            }
        }
    }

    private fun loadImage(bikeData: BikeDTO, root: View){
        val imageUrls = bikeData.images
        val viewPager = root.findViewById<ViewPager2>(R.id.viewPager)
        Log.d("ImageLoading", "Размер списка imageUrls: ${imageUrls.size}")
        val adapter = ImageBicycleAdapter(requireContext(), imageUrls)
        viewPager.adapter = adapter

        // Для эффекта карусели (необязательно)
        viewPager.offscreenPageLimit = 1
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setupData(bikeData: BikeDTO, root: View) {
        val nameBicycle = root.findViewById<TextView>(R.id.nameBicycle)
        nameBicycle.text = bikeData.name

        val weightLayout = root.findViewById<LinearLayout>(R.id.weight_layout)
        val frameMaterialLayout = root.findViewById<LinearLayout>(R.id.frame_material_layout)
        val wheelSizeLayout = root.findViewById<LinearLayout>(R.id.wheel_size_layout)
        val typeBrakesLayout = root.findViewById<LinearLayout>(R.id.type_brakes_layout)
        val typeBicycleLayout = root.findViewById<LinearLayout>(R.id.type_bicycle_layout)
        val ageLayout = root.findViewById<LinearLayout>(R.id.age_layout)
        val numberSpeedsLayout = root.findViewById<LinearLayout>(R.id.number_speeds_layout)
        val maximumLoadLayout = root.findViewById<LinearLayout>(R.id.maximum_load_layout)

        // Находим TextView для значений
        val weightValue = root.findViewById<TextView>(R.id.weight_value)
        val frameMaterialValue = root.findViewById<TextView>(R.id.frame_material_value)
        val wheelSizeValue = root.findViewById<TextView>(R.id.wheel_size_value)
        val typeBrakesValue = root.findViewById<TextView>(R.id.type_brakes_value)
        val typeBicycleValue = root.findViewById<TextView>(R.id.type_bicycle_value)
        val ageValue = root.findViewById<TextView>(R.id.age_value)
        val numberSpeedsValue = root.findViewById<TextView>(R.id.number_speeds_value)
        val maximumLoadValue = root.findViewById<TextView>(R.id.maximum_load_value)

        // Находим View для значений
        val weightDivider = root.findViewById<View>(R.id.weight_divider)
        val frameMaterialDivider = root.findViewById<View>(R.id.frame_material_divider)
        val wheelSizeDivider = root.findViewById<View>(R.id.wheel_size_divider)
        val typeBrakesDivider = root.findViewById<View>(R.id.type_brakes_divider)
        val typeBicycleDivider = root.findViewById<View>(R.id.type_bicycle_divider)
        val ageDivider = root.findViewById<View>(R.id.age_divider)
        val numberSpeedsDivider = root.findViewById<View>(R.id.number_speeds_divider)
        val maximumLoadDivider = root.findViewById<View>(R.id.maximum_load_divider)

        // Устанавливаем значения и показываем элементы, только если значение не null

        setValueAndShow(bikeData.weight, weightValue, weightLayout, weightDivider)
        setValueAndShow(bikeData.frameMaterial, frameMaterialValue, frameMaterialLayout, frameMaterialDivider)
        setValueAndShow(bikeData.wheelSize, wheelSizeValue, wheelSizeLayout, wheelSizeDivider)
        setValueAndShow(bikeData.age, ageValue, ageLayout, ageDivider)
        setValueAndShow(bikeData.numberSpeeds, numberSpeedsValue, numberSpeedsLayout, numberSpeedsDivider)
        setValueAndShow(bikeData.maximumLoad, maximumLoadValue, maximumLoadLayout, maximumLoadDivider)

        setupLoadingCharacteristic(
            id = bikeData.typeBrakes,
            layout = typeBrakesLayout,
            valueTextView = typeBrakesValue,
            divider = typeBrakesDivider
        ) { brakeId ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val brakeName = bikesService.getTypeBrakesName(brakeId)
                    withContext(Dispatchers.Main) {
                        updateLoadedValue(
                            typeBrakesLayout,
                            typeBrakesValue,
                            typeBrakesDivider,
                            brakeName)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        updateLoadedValue(
                            typeBrakesLayout,
                            typeBrakesValue,
                            typeBrakesDivider,
                            null)
                    }
                }
            }
        }

        setupLoadingCharacteristic(
            id = bikeData.typeBicycle,
            layout = typeBicycleLayout,
            valueTextView = typeBicycleValue,
            divider = typeBicycleDivider
        ) { bicycleId ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val bicycleName = bikesService.getTypeBicycleName(bicycleId)
                    withContext(Dispatchers.Main) {
                        updateLoadedValue(
                            typeBicycleLayout,
                            typeBicycleValue,
                            typeBicycleDivider,
                            bicycleName
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        updateLoadedValue(
                            typeBicycleLayout,
                            typeBicycleValue,
                            typeBicycleDivider,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun ShowToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun <T> setValueAndShow(
        value: T?,
        valueTextView: TextView,
        layout: LinearLayout,
        divider: View
    ) {
        if (value != null) {
            valueTextView.text = value.toString()
            layout.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE
        }
    }
    private fun setupLoadingCharacteristic(
        id: Int?,
        layout: LinearLayout,
        valueTextView: TextView,
        divider: View,
        startLoadingAction: (Int) -> Unit
    ) {
        if (id != null) {
            layout.isVisible = true
            divider.isVisible = true
            valueTextView.text = null
            startLoadingAction(id)
        }
    }

    private fun updateLoadedValue(
        layout: LinearLayout,
        valueTextView: TextView,
        divider: View,
        loadedValue: String?
    ) {
        if (!loadedValue.isNullOrEmpty()) {
            valueTextView.text = loadedValue
            layout.isVisible = true
            divider.isVisible = true
        }
    }
}

