package com.smarttrash.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttrash.app.data.model.AnalysisResponse
import com.smarttrash.app.data.remote.RetrofitClient
import com.smarttrash.app.data.repository.SmartTrashRepository
import com.smarttrash.app.utils.ResultState
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// ViewModel responsável por enviar a imagem da câmera ao backend e expor o estado da análise
class CameraViewModel : ViewModel() {

    private val repository = SmartTrashRepository(RetrofitClient.apiService)

    private val _analysisState = MutableLiveData<ResultState<AnalysisResponse>>()
    val analysisState: LiveData<ResultState<AnalysisResponse>> = _analysisState

    // Envia o arquivo de imagem capturado para o backend
    fun analyzeImage(imageFile: File) {
        if (!imageFile.exists()) {
            _analysisState.value = ResultState.Error("Imagem inválida. Tente capturar novamente.")
            return
        }

        _analysisState.value = ResultState.Loading

        viewModelScope.launch {
            val requestFile = imageFile
                .asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                name = "image",
                filename = imageFile.name,
                body = requestFile
            )

            val result = repository.analyzeImage(imagePart)

            _analysisState.value = result.fold(
                onSuccess = { analysis ->
                    ResultState.Success(analysis)
                },
                onFailure = { error ->
                    ResultState.Error(
                        error.message ?: "Erro ao enviar imagem para análise. Verifique sua conexão e o backend."
                    )
                }
            )
        }
    }
}
