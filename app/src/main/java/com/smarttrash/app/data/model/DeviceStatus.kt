package com.smarttrash.app.data.model

// Status do dispositivo/lixeira retornado pelo endpoint /api/device/status
// Os campos podem ser ajustados depois para casar 100% com o backend.
data class DeviceStatus(
    val status: String = "OFFLINE",
    val lastHeartbeat: String? = null,
    val description: String? = null
)
