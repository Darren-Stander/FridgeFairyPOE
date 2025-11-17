// This file defines a CameraX ImageAnalysis.Analyzer for barcode scanning.
// It uses Google ML Kit's BarcodeScanning client to process camera frames
// and returns the value of the first detected barcode via a callback.

package com.fridgefairy.android.ui.ml

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_PDF417
        )
        .build()

    private val scanner = BarcodeScanning.getClient(options)
    private var isBusy = false

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isBusy) { imageProxy.close(); return }
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        isBusy = true

        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val first = barcodes.firstOrNull()
                val value = first?.rawValue.orEmpty()
                if (value.isNotBlank()) onResult(value)
            }
            .addOnFailureListener { /* ignore preview errors */ }
            .addOnCompleteListener {
                isBusy = false
                imageProxy.close()
            }
    }
}
