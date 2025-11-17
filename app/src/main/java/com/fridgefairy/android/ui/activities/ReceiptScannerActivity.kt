// This file defines the ReceiptScannerActivity, which uses CameraX for a live preview.
// It orchestrates image analysis for either OCR (text recognition) or Barcode scanning
// and returns the detected result to the previous activity.

package com.fridgefairy.android.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fridgefairy.android.R
import com.fridgefairy.android.databinding.ActivityReceiptScannerBinding
import com.fridgefairy.android.ui.ml.BarcodeAnalyzer
import com.fridgefairy.android.ui.ml.OcrAnalyzer
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReceiptScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptScannerBinding
    private lateinit var previewView: PreviewView

    private var cameraProvider: ProcessCameraProvider? = null
    private var analysis: ImageAnalysis? = null

    private enum class Mode { OCR, BARCODE }
    private var mode: Mode = Mode.OCR

    private var throttleJob: Job? = null
    private var latestText: String = ""

    private val requestCam = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else Toast.makeText(this, "Camera permission needed", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { navigateToShoppingList() }
        } else {
            findViewById<ImageButton?>(R.id.button_back)?.setOnClickListener {
                navigateToShoppingList()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = navigateToShoppingList()
        })

        previewView = PreviewView(this).apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
            )
            keepScreenOn = true
        }
        binding.cameraContainer.addView(previewView)

        binding.btnModeOcr.setOnClickListener { switchMode(Mode.OCR) }
        binding.btnModeBarcode.setOnClickListener { switchMode(Mode.BARCODE) }
        binding.textMode.text = "Mode: OCR"

        binding.textResult.setOnClickListener {
            val currentText = binding.textResult.text.toString()
            if (currentText.isNotBlank() && currentText != "No text detected yet…") {
                returnResult(currentText)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestCam.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateToShoppingList()
        return true
    }

    private fun navigateToShoppingList() {
        startActivity(Intent(this, ShoppingListActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish()
    }

    private fun switchMode(newMode: Mode) {
        if (mode == newMode) return
        mode = newMode
        binding.textMode.text = "Mode: ${if (mode == Mode.OCR) "OCR" else "Barcode"}"
        bindAnalysisUseCase()
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            bindUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindUseCases() {
        bindAnalysisUseCase()
    }

    private fun returnResult(scannedText: String) {
        if (scannedText.isBlank()) return

        val resultIntent = Intent().apply {
            putExtra("SCANNED_TEXT", scannedText)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun bindAnalysisUseCase() {
        val provider = cameraProvider ?: return

        try { provider.unbind(analysis) } catch (_: Exception) {}

        analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val onTextResult: (String) -> Unit = { text ->
            latestText = text
            if (throttleJob?.isActive != true) {
                throttleJob = lifecycleScope.launch {
                    delay(150)
                    binding.textResult.text = latestText.ifBlank { "No text detected yet…" }
                }
            }
        }

        val onBarcodeResult: (String) -> Unit = { value ->
            binding.textResult.text = value
            returnResult(value)
        }

        when (mode) {
            Mode.OCR -> analysis?.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                OcrAnalyzer(onTextResult)
            )
            Mode.BARCODE -> analysis?.setAnalyzer(
                ContextCompat.getMainExecutor(this),
                BarcodeAnalyzer(onBarcodeResult)
            )
        }

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        provider.unbindAll()
        provider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis
        )
    }
}