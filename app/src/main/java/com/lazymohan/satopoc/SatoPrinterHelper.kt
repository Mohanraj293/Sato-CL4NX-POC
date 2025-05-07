package com.lazymohan.satopoc

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.lazymohan.satopoc.utils.PrinterCommandUtils
import com.sato.printer.Printer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket

class SatoCL4NXPrinterHelper(
    private val context: Context,
    private val onStatus: (String) -> Unit
) {
    private lateinit var printer: Printer
    private lateinit var printerLooperThread: HandlerThread

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            Printer.MESSAGE_STATE_CHANGE -> {
                when (msg.arg1) {
                    Printer.STATE_CONNECTING -> Log.d("Printer", "Connecting...")
                    Printer.STATE_CONNECTED -> Log.d("Printer", "Connected!")
                    Printer.STATE_NONE -> Log.e("Printer", "Disconnected or failed")
                }
            }

            Printer.MESSAGE_TOAST -> {
                val toastMsg = msg.data.getString("toast")
                onStatus("Toast: $toastMsg")
            }
            Printer.MESSAGE_NETWORK_DEVICE_SET -> {
                if (msg.obj == null) {
                    onStatus("No network devices found")
                } else {
                    val deviceList = msg.obj as Array<*>
                    onStatus("Found ${deviceList.size} network devices")
                    for (device in deviceList) {
                        val deviceInfo = device.toString()
                        onStatus("Device: $deviceInfo")
                    }
                }
                val toastMsg = msg.data.getString("toast")
                onStatus("Toast: $toastMsg")
            }
        }
        true
    }

    fun getNetworkPrinters() {
        printer  = Printer(context, handler, Looper.getMainLooper())
        printer.findNetworkPrinters(5000)
    }

    fun connectAndPrint(ip: String, port: Int = 9100, timeout: Int = 10000) {
        printerLooperThread = HandlerThread("CL4NXPrinterThread").apply { start() }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                printer = Printer(context, handler, printerLooperThread.looper)

                onStatus("Connecting to printer at $ip:$port...")
                printer.connect(ip, port, timeout)

                delay(1500) // Let printer settle

                if (printer.isConnected) {
                    onStatus("Sending test label to CL4NX Plus")
                    val command = PrinterCommandUtils.getCalibrationTemplate()
                    sendRawToSato(ip, 9100, command)
                }
            } catch (e: Exception) {
                onStatus("Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        if (::printer.isInitialized && printer.isConnected) printer.disconnect()
        if (::printerLooperThread.isInitialized) printerLooperThread.quitSafely()
    }

    fun sendRawToSato(ip: String, port: Int = 9100, command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 5000)

                val out = socket.getOutputStream()

                out.write(command.toByteArray())
                out.flush()

                Log.d("RawPrint", "Sent raw command to $ip:$port")

                out.close()
                socket.close()

            } catch (e: Exception) {
                Log.e("RawPrint", "Failed to send print command: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

