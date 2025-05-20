package com.savelyev.MobileApp.Utils

import android.graphics.*
import android.graphics.Bitmap.Config
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


object QrCodeGenerator {

    fun generate(
        content: String,
        size: Int,
        logo: Bitmap? = null,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        margin: Int = 1
    ): Bitmap? {
        return try {
            // 1. Генерация QR-кода
            val hints = mutableMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, margin)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            }

            val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)

            // 2. Создание Bitmap
            val bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888).apply {
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        setPixel(x, y, if (matrix.get(x, y)) foregroundColor else backgroundColor)
                    }
                }
            }

            // 3. Наложение логотипа (если есть)
            logo?.let { overlayLogo(bitmap, it) } ?: bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun overlayLogo(qrBitmap: Bitmap, logo: Bitmap): Bitmap {
        val logoSize = (qrBitmap.width * 0.2f).toInt() // 20% от размера QR

        val scaledLogo = Bitmap.createScaledBitmap(
            logo,
            logoSize,
            logoSize,
            true
        )

        val result = qrBitmap.copy(Config.ARGB_8888, true)
        val canvas = Canvas(result)

        val left = (qrBitmap.width - scaledLogo.width) / 2
        val top = (qrBitmap.height - scaledLogo.height) / 2

        canvas.drawBitmap(
            scaledLogo,
            left.toFloat(),
            top.toFloat(),
            Paint().apply { isAntiAlias = true }
        )

        return result
    }
}