package com.smarttrash.app.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.smarttrash.app.databinding.ActivityCameraBinding
import com.smarttrash.app.ui.result.ResultActivity
import com.smarttrash.app.utils.ResultState
import com.smarttrash.app.viewmodel.CameraViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Activity responsável por exibir a câmera (CameraX) e capturar a foto do objeto
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var viewModel: CameraViewModel

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        cameraExecutor = Executors.newSingleThreadExecutor()

        setupUi()
        observeAnalysis()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    // Configura listeners da tela de câmera
    private fun setupUi() {
        binding.buttonCapture.setOnClickListener { takePhoto() }
        binding.buttonClose.setOnClickListener { finish() }
    }

    // Inicializa o preview e o capture da CameraX
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also { previewUseCase ->
                previewUseCase.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Falha ao iniciar a câmera", exc)
                Toast.makeText(this, "Falha ao iniciar a câmera.", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // Realiza a captura da foto e envia para análise
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Arquivo temporário para salvar a foto
        val photoFile = File(
            externalMediaDirs.firstOrNull() ?: filesDir,
            "smarttrash-${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.progressCapture.visibility = View.VISIBLE
        binding.buttonCapture.isEnabled = false

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Erro ao salvar imagem", exception)
                    runOnUiThread {
                        binding.progressCapture.visibility = View.GONE
                        binding.buttonCapture.isEnabled = true
                        Toast.makeText(
                            this@CameraActivity,
                            "Erro ao capturar imagem. Tente novamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        // Envia o arquivo para o ViewModel fazer a chamada ao backend
                        viewModel.analyzeImage(photoFile)
                    }
                }
            }
        )
    }

    // Observa o estado da análise retornado pelo backend
    private fun observeAnalysis() {
        viewModel.analysisState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.progressCapture.visibility = View.VISIBLE
                    binding.buttonCapture.isEnabled = false
                }

                is ResultState.Success -> {
                    binding.progressCapture.visibility = View.GONE
                    binding.buttonCapture.isEnabled = true

                    val result = state.data
                    // Abre a tela de resultado com os dados retornados pela IA
                    val intent = Intent(this, ResultActivity::class.java).apply {
                        putExtra(ResultActivity.EXTRA_OBJECT, result.detectedObject)
                        putExtra(ResultActivity.EXTRA_CONFIDENCE, result.confidence.toFloat())
                        putExtra(ResultActivity.EXTRA_CAN_DISCARD, result.canDiscard)
                        putExtra(ResultActivity.EXTRA_TRASH_ACTION, result.trashAction)
                        putExtra(ResultActivity.EXTRA_ANALYSIS_ID, result.analysisId)
                    }
                    startActivity(intent)
                    finish()
                }

                is ResultState.Error -> {
                    binding.progressCapture.visibility = View.GONE
                    binding.buttonCapture.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Verifica se todas as permissões necessárias foram concedidas
    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão de câmera é necessária para capturar o objeto.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10

        // Lista de permissões necessárias para o CameraX
        private val REQUIRED_PERMISSIONS = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}
