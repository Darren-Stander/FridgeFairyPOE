package com.fridgefairy.android.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
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

        // ---- Back handling: toolbar if present, else custom ImageButton ----
        val toolbar = findViewById<MaterialToolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { navigateToShoppingList() }
        } else {
            // If you replaced the toolbar with a header like in activity_register
            findViewById<ImageButton?>(R.id.button_back)?.setOnClickListener {
                navigateToShoppingList()
            }
        }

        // System back -> go to Shopping List
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = navigateToShoppingList()
        })

        // Create PreviewView at runtime to avoid Layout Editor crashes
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
        val provider = cameraProvider ?: return
        provider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        bindAnalysisUseCase()

        provider.bindToLifecycle(
            this,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis
        )
    }

    private fun bindAnalysisUseCase() {
        val provider = cameraProvider ?: return

        try { provider.unbind(analysis) } catch (_: Exception) {}

        analysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
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
            latestText = value
            binding.textResult.text = value
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
