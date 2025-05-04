package com.savelyev.MobileApp.Utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertUserManager {
    fun showDeleteConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Да",
        negativeButtonText: String = "Нет",
        onDeleteConfirmed: () -> Unit

    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveButtonText) { dialog: DialogInterface, which: Int ->
            onDeleteConfirmed()
        }

        builder.setNegativeButton(negativeButtonText) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun showPaymentDialog(
        fragment: Fragment,
        onCardPayment: () -> Unit,
        onCashPayment: () -> Unit,
        title: String = "Выберите способ оплаты",
        messageCash: String = "Оплата наличными",
        messageCard: String = "Оплата картой",
        messageCancel: String = "Отмена",
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(title)
            .setItems(arrayOf(messageCard,messageCash)) { _, which ->
                when (which) {
                    0 -> onCardPayment()
                    1 -> onCashPayment()
                }
            }
            .setNegativeButton(messageCancel, null)
            .show()
    }

    fun showCashPaymentConfirmation(
        fragment: Fragment,
        title: String = "Оплата наличными",
        message: String = "Не забудьте оплатить заказ при получении",
        messageConfirm: String = "Подтвердить",
        messageCancel: String = "Отмена",
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(messageConfirm) { _, _ -> onConfirm() }
            .setNegativeButton(messageCancel, null)
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

}