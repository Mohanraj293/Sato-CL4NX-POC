package com.lazymohan.satopoc

data class PrinterUiState(
    val ipAddress: String = "",
    val port: String = "",
    val status: String = "Disconnected",
    val showProgress: Boolean = false,
    val printers: List<String> = emptyList(),
    val message: String? = null,
    val isConnected: Boolean = false,
)