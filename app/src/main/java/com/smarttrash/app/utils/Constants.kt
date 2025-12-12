package com.smarttrash.app.utils

// Constantes de configuração da aplicação
object Constants {
    // IMPORTANTE: ajuste este IP para o IP do backend Node.js na sua rede
    // Exemplo: "http://192.168.0.10:3000"
    const val BASE_URL = "http://192.168.0.101:3000"

    // Timeout padrão (em segundos) para requisições HTTP
    const val NETWORK_TIMEOUT_SECONDS = 30L
}
