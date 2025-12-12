package com.smarttrash.app.ui.history

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttrash.app.databinding.ActivityHistoryBinding
import com.smarttrash.app.utils.ResultState
import com.smarttrash.app.viewmodel.HistoryViewModel

// Tela de histórico das análises realizadas pela IA
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        setupRecycler()
        setupListeners()
        observeHistory()

        // Carrega o histórico ao abrir a tela
        viewModel.loadHistory()
    }

    // Configura o RecyclerView com o adapter de histórico
    private fun setupRecycler() {
        adapter = HistoryAdapter()
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter
    }

    // Configura botões da tela
    private fun setupListeners() {
        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonRetryHistory.setOnClickListener { viewModel.loadHistory() }
    }

    // Observa o estado do histórico e atualiza a UI
    private fun observeHistory() {
        viewModel.historyState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.progressHistory.visibility = View.VISIBLE
                    binding.textEmptyHistory.visibility = View.GONE
                }

                is ResultState.Success -> {
                    binding.progressHistory.visibility = View.GONE
                    val list = state.data
                    adapter.submitList(list)
                    binding.textEmptyHistory.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }

                is ResultState.Error -> {
                    binding.progressHistory.visibility = View.GONE
                    binding.textEmptyHistory.visibility = View.VISIBLE
                    binding.textEmptyHistory.text = state.message
                }
            }
        }
    }
}
