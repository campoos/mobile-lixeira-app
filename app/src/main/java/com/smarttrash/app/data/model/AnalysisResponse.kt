package com.smarttrash.app.data.model

import com.google.gson.annotations.SerializedName

// Modelo de resposta do endpoint /api/analyze
// O backend retorna o campo JSON "object", que é palavra reservada em Kotlin.
// Por isso usamos @SerializedName para mapear para a propriedade detectedObject.
data class AnalysisResponse(
    @SerializedName("object") val detectedObject: String,
    val confidence: Double,
    val canDiscard: Boolean,
    val trashAction: String,
    val analysisId: Int
)
