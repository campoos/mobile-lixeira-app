package com.smarttrash.app.data.remote

import com.smarttrash.app.data.model.AnalysisResponse
import com.smarttrash.app.data.model.DeviceStatus
import com.smarttrash.app.data.model.HistoryItem
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Interface Retrofit com os endpoints do backend SmartTrash
interface SmartTrashApiService {

    // Envia a imagem capturada para análise pela IA
    @Multipart
    @POST("/api/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): Response<AnalysisResponse>

    // Retorna o histórico de análises
    @GET("/api/history")
    suspend fun getHistory(): Response<List<HistoryItem>>

    // Retorna o status atual do dispositivo/lixeira
    @GET("/api/device/status")
    suspend fun getDeviceStatus(): Response<DeviceStatus>
}
