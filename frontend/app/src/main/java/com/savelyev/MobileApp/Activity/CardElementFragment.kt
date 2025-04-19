package com.savelyev.MobileApp.Activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.savelyev.MobileApp.Api.DTO.Response.BikeDTO
import com.savelyev.MobileApp.Api.Service.BikesService
import com.savelyev.MobileApp.CustomObject.CustomButton
import com.savelyev.MobileApp.R


class CardElementFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private val bikesService = BikesService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_card_element, container, false)

        val nameBike = arguments?.getString("name")
        fetchBikes(nameBike, root)
        val customHourButton = root.findViewById<CustomButton>(R.id.custom_hour_button)
        val customDayButton = root.findViewById<CustomButton>(R.id.custom_day_button)

        val addButton = root.findViewById<Button>(R.id.addButton)

        customHourButton.setUnitType(CustomButton.UnitType.HOUR)
        customHourButton.setPrice(200)//Хардкор TODO: брать из БД
        customDayButton.setUnitType(CustomButton.UnitType.DAY)
        customDayButton.setPrice(800)//Хардкор TODO: брать из БД
        return root
    }

    private fun fetchBikes(name: String?, root: View) {
        bikesService.getBikeData(name) { bikeData ->
            if (bikeData != null) {
                setupData(bikeData, root)
            } else {
                // Обработка ошибки
                 val a = 1
                //ShowToast("Не удалось получить список велосипедов.")
            }
        }
    }

    private fun setupData(bikeData: BikeDTO, root: View)
    {
        val nameBicycle = root.findViewById<TextView>(R.id.nameBicycle)
        nameBicycle.text = bikeData.name
    }


}