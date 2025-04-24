package com.lazymohan.satopoc

sealed class SatoPrinterEvents {
    data class ConnectPrinter(val ipAddress: String) : SatoPrinterEvents()
    object DisconnectPrinter : SatoPrinterEvents()
    object Print : SatoPrinterEvents()
    object ShowProgress : SatoPrinterEvents()
    object HideProgress : SatoPrinterEvents()
    data class ShowMessage(val message: String) : SatoPrinterEvents()
    data class UpdateStatus(val status: String) : SatoPrinterEvents()
    data class UpdatePrinters(val printers: List<String>) : SatoPrinterEvents()
    data object SearchPrinters : SatoPrinterEvents()
}