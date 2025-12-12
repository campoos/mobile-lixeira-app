package com.smarttrash.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.smarttrash.app.R
import com.smarttrash.app.databinding.ActivityMainBinding
import com.smarttrash.app.ui.camera.CameraActivity
import com.smarttrash.app.ui.history.HistoryActivity
import com.smarttrash.app.utils.ResultState
import com.smarttrash.app.viewmodel.DeviceStatusViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DeviceStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = DeviceStatusViewModel()

        setupUi()
        observeStatus()
        viewModel.loadStatus()
    }

    private fun setupUi() {
        binding.buttonCapture.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.buttonHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.buttonRetryStatus.setOnClickListener {
            viewModel.loadStatus()
        }
    }

    private fun observeStatus() {
        // --- CORES NOVAS ---
        val greenSoft = 0xFF4CAF50.toInt()     // Verde Material Design
        val greenText = 0xFF0E3B18.toInt()     // Verde escuro elegante
        val redSoft = 0xFFE53935.toInt()       // Vermelho Material Design
        val redText = 0xFF480000.toInt()       // Vermelho escuro
        val white = 0xFFFFFFFF.toInt()

        viewModel.statusState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.progressStatus.visibility = android.view.View.VISIBLE
                    binding.textStatusMessage.text = getString(R.string.loading_status)
                    binding.cardStatus.setCardBackgroundColor(white)
                    binding.textStatusTitle.setTextColor(greenText)
                    binding.textStatusMessage.setTextColor(greenText)
                }

                is ResultState.Success -> {
                    binding.progressStatus.visibility = android.view.View.GONE
                    val status = state.data.status.uppercase()
                    val isOnline = status == "ONLINE"

                    binding.textStatusTitle.text = if (isOnline) {
                        getString(R.string.status_online)
                    } else {
                        getString(R.string.status_offline)
                    }

                    val description = buildString {
                        state.data.description?.let { append(it) }
                        state.data.lastHeartbeat?.let { hb ->
                            if (isNotEmpty()) append("\n")
                            append(getString(R.string.last_heartbeat, hb))
                        }
                    }
                    binding.textStatusMessage.text = description.ifEmpty {
                        if (isOnline) getString(R.string.status_online_default) else getString(R.string.status_offline_default)
                    }

                    if (isOnline) {
                        binding.cardStatus.setCardBackgroundColor(greenSoft)
                        binding.textStatusTitle.setTextColor(greenText)
                        binding.textStatusMessage.setTextColor(greenText)
                    } else {
                        binding.cardStatus.setCardBackgroundColor(redSoft)
                        binding.textStatusTitle.setTextColor(white)
                        binding.textStatusMessage.setTextColor(redText)
                    }
                }

                is ResultState.Error -> {
                    binding.progressStatus.visibility = android.view.View.GONE
                    binding.textStatusTitle.text = getString(R.string.status_error_title)
                    binding.textStatusMessage.text = state.message
                    binding.cardStatus.setCardBackgroundColor(redSoft)
                    binding.textStatusTitle.setTextColor(white)
                    binding.textStatusMessage.setTextColor(redText)
                }
            }
        }
    }
}