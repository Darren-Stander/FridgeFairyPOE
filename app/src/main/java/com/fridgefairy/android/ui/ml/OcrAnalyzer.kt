// This file defines a CameraX ImageAnalysis.Analyzer for Optical Character Recognition (OCR).
// It uses Google ML Kit's TextRecognition client to process camera frames
// and returns the full recognized text block via a callback.

package com.fridgefairy.android.ui.ml

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrAnalyzer(
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var isBusy = false

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isBusy) { imageProxy.close(); return }
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        isBusy = true

        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)

        recognizer.process(image)
            .addOnSuccessListener { result -> onResult(result.text ?: "") }
            .addOnFailureListener { /* ignore preview errors */ }
            .addOnCompleteListener {
                isBusy = false
                imageProxy.close()
            }
    }
}
