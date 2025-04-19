package com.lazymohan.satopoc.manager

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.format.Formatter
import android.util.Log
import com.sato.printer.Printer

class SatoPrinterManager(
    private val context: Context,
    private val onPrinterConnect: (isConnected: Boolean) -> Unit,
    private val onError: (String) -> Unit
) {
    private var printer: Printer? = null
    private var isConnected = false
    private var multicastLock: WifiManager.MulticastLock? = null
    private val discoveredPrinters = mutableSetOf<String>()

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Printer.MESSAGE_STATE_CHANGE -> {
                    when (msg.arg1) {
                        Printer.STATE_CONNECTED -> {
                            isConnected = true
                            onPrinterConnect(true)
                        }

                        Printer.STATE_CONNECTING -> {
                            // Connecting in progress
                        }

                        Printer.STATE_NONE -> {
                            isConnected = false
                            onPrinterConnect(false)
                        }
                    }
                }

                Printer.MESSAGE_NETWORK_DEVICE_SET -> {
                    val devices = msg.obj as? Set<String>
                    if (devices.isNullOrEmpty()) {
                        onError("No network printers found")
                    } else {
                        discoveredPrinters.addAll(devices)
                    }
                }
                // Handle other message types as needed
            }
            true
        }
    }

    @SuppressLint("ServiceCast")
    fun initialize() {
        printer = Printer(context, handler, null)
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.connectionInfo
        Log.d("NetworkInfo", "SSID: ${info.ssid}, IP: ${Formatter.formatIpAddress(info.ipAddress)}")
        searchNetworkPrinters()
    }

    private fun searchNetworkPrinters(timeout: Int = 10000) {
        enableMulticast()
        printer?.findNetworkPrinters(timeout)
    }

    fun connectToPrinter(ipAddress: String, port: Int = 9100, timeout: Int = 5000) {
        printer?.connect(ipAddress, port, timeout)
    }

    private fun enableMulticast() {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("SatoPrinterDiscovery")
        multicastLock?.setReferenceCounted(true)
        multicastLock?.acquire()
    }

    private fun releaseMulticast() {
        multicastLock?.release()
        multicastLock = null
    }


    fun disconnect() {
        printer?.disconnect()
        isConnected = false
    }

    fun isConnected(): Boolean = isConnected

    fun printText(text: String) {
        if (!isConnected) {
            onError("Printer not connected")
            return
        }

        printer?.apply {
            clearBuffer()
            drawText(
                /* data = */ text,
                /* horizontalPosition = */ 100,
                /* verticalPosition = */ 100,
                /* fontSize = */ Printer.FONT_SIZE_10,
                /* horizontalMultiplier = */ 1,
                /* verticalMultiplier = */ 1,
                /* rightSpace = */ 0,
                /* rotation = */ Printer.ROTATION_NONE,
                /* reverse = */ false,
                /* bold = */ false,
                /* alignment = */ Printer.TEXT_ALIGNMENT_LEFT
            )
            print(1, 1)
        }
    }

    fun printBarcode(barcodeData: String) {
        if (!isConnected) {
            onError("Printer not connected")
            return
        }

        printer?.apply {
            clearBuffer()
            draw1dBarcode(
                /* data = */ barcodeData,
                /* horizontalPosition = */ 100,
                /* verticalPosition = */ 200,
                /* barcodeSelection = */ Printer.BARCODE_CODE128,
                /* narrowBarWidth = */ 1,
                /* wideBarWidth = */ 2,
                /* height = */ 240,
                /* rotation = */ Printer.ROTATION_NONE,
                /* hri = */ Printer.HRI_BELOW_FONT_SIZE_1,
                /* quietZoneWidth = */ 0
            )
            print(1, 1)
        }
    }
}