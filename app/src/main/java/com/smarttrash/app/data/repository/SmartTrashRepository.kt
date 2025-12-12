package com.smarttrash.app.data.repository

import com.smarttrash.app.data.model.AnalysisResponse
import com.smarttrash.app.data.model.DeviceStatus
import com.smarttrash.app.data.model.HistoryItem
import com.smarttrash.app.data.remote.SmartTrashApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

// Repositório responsável por orquestrar chamadas ao backend SmartTrash
class SmartTrashRepository(
    private val api: SmartTrashApiService
) {

    // Analisa uma imagem enviada pela câmera
    suspend fun analyzeImage(imagePart: MultipartBody.Part): Result<AnalysisResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.analyzeImage(imagePart)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Resposta vazia da IA."))
                    }
                } else {
                    Result.failure(Exception("Erro da IA (${response.code()})."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Recupera o histórico de análises
    suspend fun getHistory(): Result<List<HistoryItem>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getHistory()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Histórico vazio."))
                    }
                } else {
                    Result.failure(Exception("Erro ao carregar histórico (${response.code()})."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Obtém o status atual do dispositivo/lixeira
    suspend fun getDeviceStatus(): Result<DeviceStatus> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getDeviceStatus()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Status indisponível."))
                    }
                } else {
                    Result.failure(Exception("Erro ao consultar status (${response.code()})."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
