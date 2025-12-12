package com.smarttrash.app.utils

// Wrapper genérico para representar estados de carregamento/sucesso/erro nas telas
sealed class ResultState<out T> {
    // Estado de carregamento
    data object Loading : ResultState<Nothing>()

    // Estado de sucesso contendo os dados retornados
    data class Success<T>(val data: T) : ResultState<T>()

    // Estado de erro com mensagem amigável para o usuário
    data class Error(val message: String) : ResultState<Nothing>()
}
