package com.smarttrash.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttrash.app.data.model.DeviceStatus
import com.smarttrash.app.data.remote.RetrofitClient
import com.smarttrash.app.data.repository.SmartTrashRepository
import com.smarttrash.app.utils.ResultState
import kotlinx.coroutines.launch

// ViewModel responsável pelo status do dispositivo/lixeira
class DeviceStatusViewModel : ViewModel() {

    private val repository = SmartTrashRepository(RetrofitClient.apiService)

    private val _statusState = MutableLiveData<ResultState<DeviceStatus>>()
    val statusState: LiveData<ResultState<DeviceStatus>> = _statusState

    // Carrega o status do backend
    fun loadStatus() {
        _statusState.value = ResultState.Loading
        viewModelScope.launch {
            val result = repository.getDeviceStatus()
            _statusState.value = result.fold(
                onSuccess = { deviceStatus ->
                    ResultState.Success(deviceStatus)
                },
                onFailure = { error ->
                    ResultState.Error(
                        error.message ?: "Não foi possível obter o status da lixeira. Verifique se o backend está online."
                    )
                }
            )
        }
    }
}
