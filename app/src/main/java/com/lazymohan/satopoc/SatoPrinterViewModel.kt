package com.lazymohan.satopoc

import android.app.Application
import android.os.Handler
import android.os.Looper
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

    private var printer: Printer

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            Printer.MESSAGE_NETWORK_DEVICE_SET -> {
                val found = msg.obj as? Set<String> ?: emptySet()
                if (found.isNotEmpty()) {
                    _uiState.update {
                        it.copy(
                            printers = found.toMutableList()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            message = "No Network printers found"
                        )
                    }
                }
            }

            Printer.MESSAGE_STATE_CHANGE -> {
                when (msg.arg1) {
                    Printer.STATE_CONNECTED -> _uiState.update {
                        it.copy(
                            isConnected = true,
                            status = "Printer, Connected!"
                        )
                    }

                    Printer.STATE_CONNECTING -> _uiState.update {
                        it.copy(
                            isConnected = false,
                            status = "Printer, Connecting..."
                        )
                    }

                    Printer.STATE_NONE -> _uiState.update {
                        it.copy(
                            isConnected = false,
                            status = "Printer, Connection failed or disconnected!"
                        )
                    }
                }
            }
        }
        true
    }

    init {
        printer = Printer(application.applicationContext, handler, null)
        val ceh = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            _uiState.update {
                it.copy(
                    message = "Error: ${throwable.message}"
                )
            }
        }
        viewModelScope.launch(Dispatchers.IO + ceh) {
            connect("192.168.1.2")
            if (printer.isConnected) {
                _uiState.update {
                    it.copy(
                        message = "Printer, Connected!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        message = "Printer, Disconnected!"
                    )
                }
            }
        }
    }

    fun scanPrinters() {
        val ceh = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            _uiState.update {
                it.copy(
                    message = "Error: ${throwable.message}"
                )
            }
        }
        viewModelScope.launch(ceh) {
            try {
                withContext(Dispatchers.IO) {
                    printer.findNetworkPrinters(5000) // Ensure this runs on a background thread
                }
                _uiState.update {
                    it.copy(
                        message = "Scanning for printers..."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        message = "Error scanning printers: ${e.message}"
                    )
                }
            }
        }
    }

    fun connect(ip: String) {
        val ceh = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            _uiState.update {
                it.copy(
                    message = "Error: ${throwable.message}"
                )
            }
        }
        viewModelScope.launch(ceh) {
            try {
                withContext(Dispatchers.IO) {
                    printer.connect(ip, 9100, 5000) // Ensure this runs on a background thread
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        message = "Error connecting to printer: ${e.message}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        printer.disconnect()
    }
}