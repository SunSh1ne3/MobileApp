package com.savelyev.MobileApp.CustomObject

import android.annotation.SuppressLint
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

    interface OnCountChangedListener {
        fun onCountChanged(totalPrice: Int)
    }

    private var listener: OnCountChangedListener? = null
    private var currentCount: Int = 0
    private var pricePerUnit: Int = 200
    private var minusButton: ImageView
    private var plusButton: ImageView
    private var countText: TextView
    private var priceText: TextView
    private var unitType: UnitType = UnitType.HOUR

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_button, this, true)

        minusButton = findViewById(R.id.minus_button)
        plusButton = findViewById(R.id.plus_button)
        countText = findViewById(R.id.count_text)
        priceText = findViewById(R.id.price_text)

        updateUI()

        minusButton.setOnClickListener {
            currentCount -=1
            updateUI()
            notifyCountChanged()
        }
        plusButton.setOnClickListener {
            currentCount +=1
            updateUI()
            notifyCountChanged()
        }
    }

    fun setOnCountChangedListener(listener: OnCountChangedListener) {
        this.listener = listener
    }

    private fun updateUI() {
        setCountText(currentCount)
    }

    @SuppressLint("SetTextI18n")
    fun setPrice(price: Int) {
        pricePerUnit = price
        priceText.text = "$pricePerUnit р"
    }

    @SuppressLint("SetTextI18n")
    fun setCountText(count: Int) {
        currentCount = count
        if (currentCount < 0) {
            currentCount = 0
        }
        countText.text = "$currentCount ${pluralText()}"
    }

    fun setUnitType(type: UnitType) {
        unitType = type
        setCountText(currentCount)
    }

    private fun notifyCountChanged() {
        listener?.onCountChanged(currentCount * pricePerUnit)
    }

    private fun pluralText(): String {
        return when(unitType){
            UnitType.HOUR -> pluralHours()
            UnitType.DAY -> pluralDays()
        }
    }

    private fun pluralHours(): String {
        return when {
            currentCount % 100 in 11..14 -> "часов"
            currentCount % 10 == 1 -> "час"
            currentCount % 10 in 2..4 -> "часа"
            else -> "часов"
        }
    }

    private fun pluralDays(): String {
        return when {
            currentCount % 100 in 11..14 -> "дней"
            currentCount % 10 == 1 -> "день"
            currentCount % 10 in 2..4 -> "дня"
            else -> "дней"
        }
    }

    enum class UnitType(val unitName: String) {
        HOUR("час"),
        DAY("день")
    }

    fun getCurrentCount(): Int {
        return currentCount
    }
    fun getPrice(): Int {
        return currentCount * pricePerUnit
    }

}
