package com.smarttrash.app.data.model

import com.google.gson.annotations.SerializedName

// Item de histórico retornado pelo endpoint /api/history
// Estrutura assumida com base nos requisitos funcionais do app.
data class HistoryItem(
    @SerializedName("object") val detectedObject: String,
    val trashAction: String,
    val canDiscard: Boolean,
    val confidence: Double,
    val analysisId: Int,
    val createdAt: String? = null
)
