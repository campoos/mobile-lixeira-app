package com.smarttrash.app.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.smarttrash.app.R
import com.smarttrash.app.databinding.ActivityResultBinding
import com.smarttrash.app.ui.main.MainActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
        bindFromIntent()
    }

    private fun setupUi() {
        binding.buttonBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun bindFromIntent() {
        val detectedObject = intent.getStringExtra(EXTRA_OBJECT) ?: "Objeto desconhecido"
        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0f)
        val canDiscard = intent.getBooleanExtra(EXTRA_CAN_DISCARD, false)
        val trashAction = intent.getStringExtra(EXTRA_TRASH_ACTION) ?: "DENY"
        val analysisId = intent.getIntExtra(EXTRA_ANALYSIS_ID, -1)

        val isOpen = trashAction.equals("OPEN", ignoreCase = true) && canDiscard

        // --- NOVAS CORES ---
        val greenSoft = 0xFF4CAF50.toInt()     // Verde Material Design
        val greenText = 0xFF0E3B18.toInt()     // Verde escuro elegante
        val redSoft = 0xFFE53935.toInt()       // Vermelho Material Design
        val redText = 0xFF480000.toInt()       // Vermelho escuro pra dar contraste
        val white = 0xFFFFFFFF.toInt()

        if (isOpen) {
            binding.textResultTitle.text = getString(R.string.result_discard_allowed_title)
            binding.textResultMessage.text =
                getString(R.string.result_discard_allowed_message, detectedObject)
            binding.textResultIcon.text = "✅"

            binding.cardResult.setCardBackgroundColor(greenSoft)
            applyTextColor(greenText)

        } else {
            binding.textResultTitle.text = getString(R.string.result_discard_denied_title)
            binding.textResultMessage.text =
                getString(R.string.result_discard_denied_message, detectedObject)
            binding.textResultIcon.text = "❌"

            binding.cardResult.setCardBackgroundColor(redSoft)
            applyTextColor(white)
            binding.textResultIcon.setTextColor(redText)
        }

        binding.textObjectName.text = detectedObject
        val confidencePercent = (confidence * 100).toInt()
        binding.textConfidence.text = getString(R.string.confidence_format, confidencePercent)

        if (analysisId >= 0) {
            binding.textAnalysisId.text =
                getString(R.string.analysis_id_format, analysisId)
        }
    }

    private fun applyTextColor(color: Int) {
        binding.textResultTitle.setTextColor(color)
        binding.textResultMessage.setTextColor(color)
        binding.textObjectName.setTextColor(color)
        binding.textConfidence.setTextColor(color)
        binding.textAnalysisId.setTextColor(color)
        binding.textResultIcon.setTextColor(color)
    }

    companion object {
        const val EXTRA_OBJECT = "extra_object"
        const val EXTRA_CONFIDENCE = "extra_confidence"
        const val EXTRA_CAN_DISCARD = "extra_can_discard"
        const val EXTRA_TRASH_ACTION = "extra_trash_action"
        const val EXTRA_ANALYSIS_ID = "extra_analysis_id"
    }
}