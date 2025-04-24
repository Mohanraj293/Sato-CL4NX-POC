package com.lazymohan.satopoc

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sato.printer.Printer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SatoPrinterViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(PrinterUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var printer: Printer
    private val handler = Handler(Looper.getMainLooper()) { handlePrinterMessage(it) }
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        updateState(message = "Error: ${throwable.message}")
        throwable.printStackTrace()
    }

    init {
        initializePrinter()
    }

    private fun initializePrinter() {
        printer = Printer(getApplication(), handler, null)
        printer.initializePrinter()
        checkInitialConnectionStatus()
    }

    private fun checkInitialConnectionStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = printer.isConnected
            updateState(
                isConnected = isConnected,
                status = if (isConnected) "Printer connected" else "Printer disconnected"
            )
        }
    }

    private fun handlePrinterMessage(msg: Message): Boolean {
        return when (msg.what) {
            Printer.MESSAGE_NETWORK_DEVICE_SET -> {
                val foundPrinters = (msg.obj as? Set<String>)?.toList() ?: emptyList()
                if (foundPrinters.isNotEmpty()) {
                    updateState(printers = foundPrinters)
                } else {
                    updateState(message = "No network printers found")
                }
                true
            }
            Printer.MESSAGE_STATE_CHANGE -> {
                when (msg.arg1) {
                    Printer.STATE_CONNECTED -> updateState(
                        isConnected = true,
                        message = "Printer connected successfully"
                    )
                    Printer.STATE_CONNECTING -> updateState(message = "Connecting to printer...")
                    Printer.STATE_NONE -> updateState(
                        isConnected = false,
                        message = "Connection failed or disconnected"
                    )
                }
                true
            }
            else -> false
        }
    }

    fun scanPrinters(timeout: Int = 10000) {
        viewModelScope.launch(exceptionHandler) {
            updateState(message = "Scanning for printers...")
            try {
                withContext(Dispatchers.IO) {
                    printer.findNetworkPrinters(timeout)
                }
            } catch (e: Exception) {
                updateState(message = "Scan failed: ${e.message}")
            }
        }
    }

    fun connect(ip: String, port: Int = 9100, timeout: Int = 5000) {
        viewModelScope.launch(exceptionHandler) {
            updateState(message = "Connecting to $ip...")
            try {
                withContext(Dispatchers.IO) {
                    printer.connect(ip, port, timeout)
                }
            } catch (e: Exception) {
                updateState(
                    isConnected = false,
                    message = "Connection failed: ${e.message}"
                )
            }
        }
    }

    private fun updateState(
        isConnected: Boolean = _uiState.value.isConnected,
        printers: List<String> = _uiState.value.printers,
        message: String? = null,
        status: String? = null,
    ) {
        _uiState.update {
            it.copy(
                isConnected = isConnected,
                printers = printers.toMutableList(),
                message = message ?: it.message,
                status = status.orEmpty()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        printer.disconnect()
    }
}