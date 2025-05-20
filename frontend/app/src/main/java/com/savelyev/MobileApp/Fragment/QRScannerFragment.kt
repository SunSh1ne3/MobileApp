package com.savelyev.MobileApp.Fragment

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.savelyev.MobileApp.Api.DTO.QrData
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.QrCodeAnalyzer
import com.savelyev.MobileApp.databinding.FragmentQrScannerBinding
import kotlinx.serialization.json.Json

class QrScannerFragment : Fragment() {
    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentAnimator: ValueAnimator? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var frameRect: Rect

    private var isProcessingQr = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFrame()
        setupControls()
        requestCameraPermission()
    }

    private fun setupFrame() {
        binding.qrFrame.post {
            val location = IntArray(2)
            binding.qrFrame.getLocationOnScreen(location)
            frameRect = Rect(
                location[0],
                location[1],
                location[0] + binding.qrFrame.width,
                location[1] + binding.qrFrame.height
            )
            Log.d("QR_POS", "Calculated frame rect: $frameRect")

        }
    }

    private fun setupControls() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationale()
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    private fun showPermissionRationale() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Требуется доступ к камере")
            .setMessage("Для сканирования QR-кодов необходимо разрешение на использование камеры")
            .setPositiveButton("Разрешить") { _, _ ->
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
            .setNegativeButton("Отмена") { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                PushManager.showToast( "Для сканирования QR-кода нужен доступ к камере")
                findNavController().popBackStack()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(requireContext()),
                        QrCodeAnalyzer(
                            frameRect = frameRect,
                            previewWidth = binding.previewView.width,
                            previewHeight = binding.previewView.height
                        ) { qrContent ->
                            if (isAdded  && _binding != null) {
                                handleScannedQr(qrContent)
                            }
                        })
                }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                PushManager.showToast( "Ошибка запуска камеры")
                findNavController().popBackStack()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun handleScannedQr(qrContent: String) {
        if (isProcessingQr || _binding == null) return

        Log.d("QR_DEBUG", "Handling scanned QR: $qrContent")

        try {
            // 1. Попробуем распарсить как JSON объект (старый формат)
            if (qrContent.startsWith("{") && qrContent.endsWith("}")) {
                val qrData = Json.decodeFromString<QrData>(qrContent)
                processValidQr(qrData)
                return
            }


            // 2. Попробуем распарсить как строку в формате "Scanner closed: QrData(...)"
            val regex = """Scanner closed: QrData\((.*?)\)""".toRegex()
            val matchResult = regex.find(qrContent)

            if (matchResult != null) {
                val qrDataStr = matchResult.groupValues[1]
                val qrData = parseQrDataFromString(qrDataStr)
                processValidQr(qrData)
                return
            }

            // 3. Если ни один формат не подошел
            showInvalidQrError("Неподдерживаемый формат QR-кода")

        } catch (e: Exception) {
            Log.e("QR_DEBUG", "QR processing error", e)
            showInvalidQrError()
            isProcessingQr = false
        }
    }

    private fun processValidQr(qrData: QrData) {
        Log.d("QR_DEBUG", "Valid QR data: $qrData")
        safeAnimateFrame(Color.GREEN)
        Log.d("QR_DEBUG", "Visual feedback: green frame")

        handler.postDelayed({
            Log.d("QR_DEBUG", "Sending result back")
            if (isAdded && _binding != null) {
                parentFragmentManager.setFragmentResult(
                    QR_SCAN_RESULT,
                    bundleOf("qr_data" to qrData)
                )
                Log.d("QR_DEBUG", "Result sent, popping back")
                findNavController().popBackStack()
                Log.d("QR_DEBUG", "Scanner closed: $qrData")
            } else {
                Log.e("QR_DEBUG", "Cannot send result - fragment detached")
            }
            isProcessingQr = false
        }, 300)
    }

    private fun parseQrDataFromString(dataStr: String): QrData {
        // Пример строки: "userId=45, orderIds=[134, 136]"
        val userId = """userId=(\d+)""".toRegex().find(dataStr)?.groupValues?.get(1)?.toInt()
            ?: throw IllegalArgumentException("Missing userId")

        val orderIds = """orderIds=\[([\d,\s]+)\]""".toRegex()
            .find(dataStr)?.groupValues?.get(1)
            ?.split(",")
            ?.map { it.trim().toInt() }
            ?: throw IllegalArgumentException("Missing orderIds")

        return QrData(userId, orderIds)
    }

    private fun safeAnimateFrame(color: Int) {
        // Отменяем предыдущую анимацию
        currentAnimator?.cancel()

        // Проверяем, что binding доступен
        if (_binding == null) return

        currentAnimator = ValueAnimator.ofArgb(Color.WHITE, color).apply {
            duration = 200
            addUpdateListener { animator ->
                if (_binding != null) {
                    binding.qrFrame.setColorFilter(animator.animatedValue as Int)
                } else {
                    cancel()
                }
            }
            start()
        }
    }

    private fun showInvalidQrError(message: String = "Неверный QR-код") {
        safeAnimateFrame(Color.RED)
        PushManager.showToast(message)
        Handler(Looper.getMainLooper()).postDelayed({
            safeAnimateFrame(Color.WHITE)
        }, 1000)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        currentAnimator?.cancel()
        cameraProvider?.unbindAll()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 101
        const val QR_SCAN_RESULT = "qr_scan_result"
    }
}