package com.savelyev.MobileApp.Utils

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val frameRect: Rect?,
    private val previewWidth: Int,
    private val previewHeight: Int,
    private val onQrScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAllPotentialBarcodes()
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (frameRect == null) {
            imageProxy.close()
            return
        }
        try {
            val mediaImage = imageProxy.image
            if (mediaImage == null) {
                Log.d("QR_DEBUG", "Frame skipped: mediaImage is null")
                imageProxy.close()
                return
            }

            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes: List<Barcode> ->
                    barcodes.firstOrNull { barcode ->
                        isQrInFrame(barcode, imageProxy)
                    }?.rawValue?.let(onQrScanned)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            Log.e("QR_DEBUG", "Analyze failed", e)
            imageProxy.close()
        }
    }

    private fun isQrInFrame(barcode: Barcode, imageProxy: ImageProxy): Boolean {
        val boundingBox = barcode.boundingBox ?: run {
            Log.d("QR_POS", "Barcode has no bounding box")
            return false
        }

        // 1. Нормализуем координаты QR-кода к диапазону [0,1]
        val qrLeft = boundingBox.left.toFloat() / imageProxy.width
        val qrTop = boundingBox.top.toFloat() / imageProxy.height
        val qrRight = boundingBox.right.toFloat() / imageProxy.width
        val qrBottom = boundingBox.bottom.toFloat() / imageProxy.height

        // 2. Корректируем координаты с учетом поворота
        val (normalizedLeft, normalizedTop, normalizedRight, normalizedBottom) = when (imageProxy.imageInfo.rotationDegrees) {
            90 -> Quad(
                y = qrTop,
                x = 1f - qrRight,
                y2 = qrBottom,
                x2 = 1f - qrLeft
            )
            180 -> Quad(
                x = 1f - qrRight,
                y = 1f - qrBottom,
                x2 = 1f - qrLeft,
                y2 = 1f - qrTop
            )
            270 -> Quad(
                y = 1f - qrBottom,
                x = qrLeft,
                y2 = 1f - qrTop,
                x2 = qrRight
            )
            else -> Quad(
                x = qrLeft,
                y = qrTop,
                x2 = qrRight,
                y2 = qrBottom
            )
        }

        // 3. Нормализуем координаты рамки
        val frameLeft = frameRect?.left?.toFloat()?.div(previewWidth)
        val frameTop = frameRect?.top?.toFloat()?.div(previewHeight)
        val frameRight = frameRect?.right?.toFloat()?.div(previewWidth)
        val frameBottom = frameRect?.bottom?.toFloat()?.div(previewHeight)

        // 4. Проверяем пересечение
        val intersects = !(normalizedRight < frameLeft!! ||
                normalizedLeft > frameRight!! ||
                normalizedBottom < frameTop!! ||
                normalizedTop > frameBottom!!)

        if (!intersects) {
            Log.d("QR_POS", "QR code doesn't intersect with frame")
            return false
        }

        // 5. Вычисляем площадь пересечения
        val overlapLeft = maxOf(normalizedLeft, frameLeft)
        val overlapTop = maxOf(normalizedTop, frameTop!!)
        val overlapRight = minOf(normalizedRight, frameRight!!)
        val overlapBottom = minOf(normalizedBottom, frameBottom!!)

        val qrArea = (normalizedRight - normalizedLeft) * (normalizedBottom - normalizedTop)
        val intersectionArea = (overlapRight - overlapLeft) * (overlapBottom - overlapTop)
        val coverage = intersectionArea / qrArea

        Log.d("QR_POS", "Coverage: ${"%.1f".format(coverage * 100)}%")
        return coverage >= 0.8f
    }

    // Вспомогательный класс для хранения координат
    private data class Quad(
        val x: Float,
        val y: Float,
        val x2: Float,
        val y2: Float
    )
}