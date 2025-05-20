package com.savelyev.MobileApp.CustomObject

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.savelyev.MobileApp.R

class PaymentDialog(
    context: Context,
    private val onCardPayment: () -> Unit,
    private val onCashPayment: () -> Unit,
    private val title: String = context.getString(R.string.PAYMENT_TITLE),
    private val messageCash: String = context.getString(R.string.CASH_PAYMENT_TITLE),
    private val messageCard: String = context.getString(R.string.CARD_PAYMENT_TITLE),
    private val messageCancel: String = context.getString(R.string.CANCEL),
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_dialog_payment)

        // Инициализация элементов
        setupText()

        findViewById<MaterialButton>(R.id.btnCardPayment).setOnClickListener {
            Toast.makeText(context, "Оплата картой будет доступна в следующем обновлении", Toast.LENGTH_SHORT).show()
        }

        findViewById<MaterialButton>(R.id.btnCashPayment).setOnClickListener {
            onCashPayment()
            dismiss()
        }

        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }
    }

    private fun setupText(){
        val titleView = findViewById<TextView>(R.id.titleDialog)
        val cashButton = findViewById<MaterialButton>(R.id.btnCashPayment)
        val cardButton = findViewById<MaterialButton>(R.id.btnCardPayment)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)

        // Установка текста
        titleView.text = title
        cashButton.text = messageCash
        cardButton.text = messageCard
        btnCancel.text = messageCancel
    }
}