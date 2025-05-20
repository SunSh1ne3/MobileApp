package com.savelyev.MobileApp.Utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.savelyev.MobileApp.Api.DTO.OrderDTO
import com.savelyev.MobileApp.Api.DTO.QrData
import com.savelyev.MobileApp.CustomObject.PaymentDialog
import com.savelyev.MobileApp.R
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AlertUserManager {
    fun showDeleteConfirmationDialog(
        context: Context,
        title: String = context.getString(R.string.CONFIRM_DELETE_TITLE),
        message: String = context.getString(R.string.CONFIRM_DELETE_MESSAGE),
        positiveButtonText: String = context.getString(R.string.YES),
        negativeButtonText: String = context.getString(R.string.NO),
        onDeleteConfirmed: () -> Unit

    ) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> onDeleteConfirmed() }
            .setNegativeButton(negativeButtonText, null)
            .show()
    }

    fun showInformationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = context.getString(R.string.OK),
        onConfirmed: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> onConfirmed?.invoke() }
            .show()
    }

    fun showPaymentDialog(
        fragment: Fragment,
        onCardPayment: () -> Unit,
        onCashPayment: () -> Unit
    ) {
        PaymentDialog(
            fragment.requireContext(),
            onCardPayment,
            onCashPayment
        ).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    fun showConfirmationDialog(
        fragment: Fragment,
        title: String,
        message: String,
        positiveButtonText: String = fragment.getString(R.string.CONFIRM),
        negativeButtonText: String = fragment.getString(R.string.CANCEL),
        onConfirmed: () -> Unit
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> onConfirmed() }
            .setNegativeButton(negativeButtonText, null)
            .show()
    }

    fun showComingSoonDialog(
        fragment: Fragment,
        title: String = "Скоро будет доступно",
        message: String = "Оплата картой находится в разработке",
        messageConfirm: String = "OK"
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(messageConfirm) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun showCompactQrDialog(
        fragment: Fragment,
        orders: List<OrderDTO>,
        qrSize: Int = 250 // Размер QR-кода в dp
    ) {
        try {
            val userId = UserManager.getCurrentUser()?.id ?: run {
                PushManager.showToast("Пользователь не авторизован")
                return
            }
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }

            val qrData = json.encodeToString(QrData(userId, orders.map { it.id }))

            MaterialAlertDialogBuilder(fragment.requireContext(), R.style.QRDialogTheme)
                .setView(R.layout.dialog_qr)
                .setCancelable(true)
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(android.R.color.transparent)
                    setOnShowListener {
                        try {
                            findViewById<ImageView>(R.id.qr_image)?.setImageBitmap(
                                QrCodeGenerator.generate(qrData, qrSize)
                            )
                            findViewById<TextView>(R.id.cancel_button)?.setOnClickListener { dismiss() }
                        } catch (e: Exception) {
                            dismiss()
                            PushManager.showToast("Ошибка отображения QR-кода")
                            e.printStackTrace()
                        }
                    }
                    show()
                }


        } catch (e: Exception) {
            PushManager.showToast("Ошибка создания QR-кода")
            e.printStackTrace()
        }
    }



}