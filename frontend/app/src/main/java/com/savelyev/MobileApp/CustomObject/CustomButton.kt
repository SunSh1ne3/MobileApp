package com.savelyev.MobileApp.CustomObject

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView
import com.savelyev.MobileApp.R

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var textHeader: TextView
    private var textPrice: TextView
    private var minusButton: ImageView
    private var plusButton: ImageView
    private var currentCount: Int = 1

    private var unitType: UnitType = UnitType.HOUR
    private var pricePerUnit: Int = 200
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_button, this, true)

        textHeader = findViewById(R.id.count_text)
        textPrice = findViewById(R.id.price_text)
        minusButton = findViewById(R.id.minus_button)
        plusButton = findViewById(R.id.plus_button)

        setCountText(currentCount)
        setPrice(pricePerUnit)

        // Устанавливаем обработчики нажатий
        minusButton.setOnClickListener {
            updateCountText(-1)
            updatePrice()
        }
        plusButton.setOnClickListener {
            updateCountText(1)
            updatePrice()
        }
    }

    private fun updateCountText(change: Int) {
        currentCount += change
        if (currentCount < 1) {
            currentCount = 1
        }
       // textHeader.text = "$currentCount час${if (currentCount != 1) "а" else ""}"
        textHeader.text = "${currentCount} ${unitType.unitName}"
    }

    private fun updatePrice() {
        val price = currentCount * pricePerUnit
        textPrice.text = "$price р"
    }

    fun setPrice(price: Int) {
        pricePerUnit = price
        textPrice.text = "${pricePerUnit} р"
    }

    fun setCountText(count: Int) {
        currentCount = count
        if (currentCount < 1) {
            currentCount = 1
        }
        // textHeader.text = "$currentCount час${if (currentCount != 1) "а" else ""}"
        textHeader.text = "${currentCount} ${unitType.unitName}"
    }

    fun setUnitType(type: UnitType) {
        unitType = type
        setCountText(currentCount)
    }

    enum class UnitType(val unitName: String) {
        HOUR("час"),
        DAY("день")
    }
}
