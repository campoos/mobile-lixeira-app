package com.smarttrash.app.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smarttrash.app.R
import com.smarttrash.app.data.model.HistoryItem
import com.smarttrash.app.databinding.ItemHistoryBinding

// Adapter responsável por exibir a lista de histórico de análises
class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem.analysisId == newItem.analysisId

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ViewHolder responsável por popular os dados de cada item da lista
    class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryItem) {
            val context = binding.root.context

            binding.textObjectName.text = item.detectedObject

            val isOpen = item.trashAction.equals("OPEN", ignoreCase = true)
            val resultText = if (isOpen) {
                context.getString(R.string.discard_allowed)
            } else {
                context.getString(R.string.discard_denied)
            }
            binding.textResult.text = resultText

            val confidencePercent = (item.confidence * 100).toInt()
            binding.textConfidence.text = context.getString(R.string.confidence_format, confidencePercent)

            // Data de criação, se existir
            binding.textDate.text = item.createdAt ?: context.getString(R.string.history_no_date)

            // Cores do card conforme o resultado
            val bgColor = if (isOpen) R.color.status_online else R.color.status_offline
            binding.cardHistory.setCardBackgroundColor(ContextCompat.getColor(context, bgColor))
        }
    }
}
