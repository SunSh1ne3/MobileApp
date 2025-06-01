package com.savelyev.MobileApp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.savelyev.MobileApp.Api.DTO.BikeDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.databinding.FragmentAddBikeBinding
import kotlin.coroutines.resume

class AddBicycleFragment : Fragment() {
    private val bikesService = BikesService()
    private var _binding: FragmentAddBikeBinding? = null
    private val binding get() = _binding!!

    private lateinit var brakesAdapter: ArrayAdapter<String>
    private lateinit var bicycleTypesAdapter: ArrayAdapter<String>
    private val brakesList = mutableListOf<Pair<Int, String>>()
    private val bicycleTypesList = mutableListOf<Pair<Int, String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBikeBinding.inflate(inflater, container, false)
        setupUI()
        loadSpinnerData()


        return binding.root
    }

    private fun setupUI() {
        binding.btnSubmit.setOnClickListener {
            if (validateForm()) {
                submitBike()
            }
        }
    }

    private fun validateForm(): Boolean {
        return when {
            binding.etName.text.isNullOrEmpty() -> {
                binding.etName.error = getString(R.string.MSG_ERROR_EMPTY_NAME)
                false
            }
            brakesList.isNotEmpty() && binding.spinnerBrakes.selectedItemPosition == 0 -> {
                PushManager.showToast("Выберите тип тормозной системы")
                false
            }
            bicycleTypesList.isNotEmpty() && binding.spinnerBrakes.selectedItemPosition == 0 -> {
                PushManager.showToast("Выберите тип тип велосипеда")
                false
            }
            else -> true
        }
    }

    private fun submitBike() {
        val bike = BikeDTO(
            id = 0,
            name = binding.etName.text.toString(),
            weight = parseDoubleSafely(binding.etWeight.text?.toString()),
            frameMaterial = binding.etFrameMaterial.text?.toString(),
            wheelSize = binding.etWheelSize.text?.toString(),

            typeBrakes = getSelectedId(binding.spinnerBrakes, brakesList),
            typeBicycle = getSelectedId(binding.spinnerBicycleType, bicycleTypesList),

            age = parseIntSafely(binding.etAge.text?.toString()),
            numberSpeeds = parseIntSafely(binding.etSpeeds.text?.toString()),
            maximumLoad = parseIntSafely(binding.etMaxLoad.text?.toString()),
            images = emptyList() // Заглушка для примера
        )

        // Отправка на бэкенд
        bikesService.addBicycle(bike) { response ->
            if (response != null) {
                PushManager.showToast("Велосипед ${response.name} успешно добавлен.")
            } else {
                PushManager.showToast("Не удалось добавить велосипед")
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun loadSpinnerData() {
        bikesService.getTypesBrakesBicycle { brakes ->
            activity?.runOnUiThread {
                when {
                    !brakes.isNullOrEmpty() -> {
                        brakesList.clear()
                        brakesList.add(Pair(0, "Выберите тип тормозной системы"))
                        brakes.forEach { brakesList.add(Pair(it.id, it.name)) }
                        setupSpinner(binding.spinnerBrakes, brakesList)

//                        brakesList.clear()
//                        brakes.forEach { brakesList.add(Pair(it.id, it.name)) }
//                        brakesAdapter = ArrayAdapter(
//                            requireContext(),
//                            R.drawable.spinner_item,
//                            brakesList.map { it.second }
//                        ).apply {
//                            setDropDownViewResource(R.drawable.spinner_item)
//                        }
//                        binding.spinnerBrakes.adapter = brakesAdapter
                    }

//                    brakes != null && brakes.isEmpty() -> {
//                        val emptyAdapter = ArrayAdapter.createFromResource(
//                            requireContext(),
//                            R.string.EMPTY_DATA_FIELD,
//                            R.drawable.spinner_item
//                        ).apply {
//                            setDropDownViewResource(R.drawable.spinner_item)
//                        }
//                        binding.spinnerBrakes.adapter = emptyAdapter
//                        binding.spinnerBrakes.isEnabled = false
//                    }

                    else -> {
                        setupEmptySpinner(binding.spinnerBrakes, R.string.EMPTY_DATA_FIELD)
                        PushManager.showToast("Не удалось загрузить список велосипедов")
                    }
                }
            }
        }

        bikesService.getTypesBicycle { types ->
            activity?.runOnUiThread {
                when {
                    !types.isNullOrEmpty() -> {
                        bicycleTypesList.clear()
                        bicycleTypesList.add(Pair(0, "Выберите тип велосипеда"))
                        types.forEach { bicycleTypesList.add(Pair(it.id, it.name)) }
                        setupSpinner(binding.spinnerBicycleType, bicycleTypesList)
                    }
                    else -> setupEmptySpinner(binding.spinnerBicycleType, R.string.EMPTY_DATA_FIELD)
//                    !types.isNullOrEmpty() -> {
//                        bicycleTypesList.clear()
//                        types.forEach { bicycleTypesList.add(Pair(it.id, it.name)) }
//                        bicycleTypesAdapter = ArrayAdapter(
//                            requireContext(),
//                            R.drawable.spinner_item,
//                            bicycleTypesList.map { it.second }
//                        ).apply {
//                            setDropDownViewResource(R.drawable.spinner_item)
//                        }
//                        binding.spinnerBicycleType.adapter = bicycleTypesAdapter
//                    }
//
//                    types != null && types.isEmpty() -> {
//                        val emptyAdapter = ArrayAdapter.createFromResource(
//                            requireContext(),
//                            R.string.EMPTY_DATA_FIELD,
//                            R.drawable.spinner_item
//                        ).apply {
//                        }
//                        binding.spinnerBicycleType.adapter = emptyAdapter
//                        binding.spinnerBicycleType.isEnabled = false
//                    }
//
//                    else -> {
//                        PushManager.showToast("Не удалось загрузить типы велосипедов")
//                    }
                }
            }
        }
    }


    private fun parseDoubleSafely(value: String?): Double? {
        return try {
            value?.takeIf { it.isNotEmpty() }?.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun parseIntSafely(value: String?): Int? {
        return try {
            value?.takeIf { it.isNotEmpty() }?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun getSelectedId(spinner: AppCompatSpinner, items: List<Pair<Int, String>>): Int? {
        return if (items.isEmpty()) null else items[spinner.selectedItemPosition].first
    }

    @SuppressLint("ResourceType")
    private fun setupSpinner(spinner: AppCompatSpinner, items: List<Pair<Int, String>>) {
        ArrayAdapter(
            requireContext(),
            R.drawable.spinner_item,
            items.map { it.second }
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_item)
            spinner.adapter = adapter
            spinner.isEnabled = true
        }
    }

    @SuppressLint("ResourceType")
    private fun setupEmptySpinner(spinner: AppCompatSpinner, emptyMessageRes: Int) {
        ArrayAdapter.createFromResource(
            requireContext(),
            emptyMessageRes,
            R.drawable.spinner_item,
        ).also { adapter ->
            adapter.setDropDownViewResource(R.drawable.spinner_item)
            spinner.adapter = adapter
            spinner.isEnabled = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}