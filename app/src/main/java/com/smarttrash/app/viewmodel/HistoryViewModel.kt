package com.smarttrash.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttrash.app.data.model.HistoryItem
import com.smarttrash.app.data.remote.RetrofitClient
import com.smarttrash.app.data.repository.SmartTrashRepository
import com.smarttrash.app.utils.ResultState
import kotlinx.coroutines.launch

// ViewModel responsável por carregar o histórico de análises
class HistoryViewModel : ViewModel() {

    private val repository = SmartTrashRepository(RetrofitClient.apiService)

    private val _historyState = MutableLiveData<ResultState<List<HistoryItem>>>()
    val historyState: LiveData<ResultState<List<HistoryItem>>> = _historyState

    // Faz a requisição de histórico para o backend
    fun loadHistory() {
        _historyState.value = ResultState.Loading
        viewModelScope.launch {
            val result = repository.getHistory()
            _historyState.value = result.fold(
                onSuccess = { list ->
                    ResultState.Success(list)
                },
                onFailure = { error ->
                    ResultState.Error(
                        error.message ?: "Não foi possível carregar o histórico. Verifique o backend e sua conexão."
                    )
                }
            )
        }
    }
}
