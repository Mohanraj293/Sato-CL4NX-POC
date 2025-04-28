package com.lazymohan.satopoc

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.sato.printer.Printer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStreamWriter
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
        }
        true
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

                    Log.d("Printer", "Connected. Sending label...")


                    val sbpl = """
    \u001BA
    \u001BH0100
    \u001BV0100
    \u001BL0101
    \u001BXU
    Hello CL4NX
    \u001BQ1
    \u001BZ
""".trimIndent()

                    sendRawToSato1("192.168.0.146", 9100, sbpl)


//                    printer.drawText(
//                        /* data = */ "Device Font Text test !!",
//                        /* horizontalPosition = */ 100,
//                        /* verticalPosition = */ 80,
//                        /* fontSize = */ Printer.FONT_SIZE_10,
//                        /* horizontalMultiplier = */ 1,
//                        /* verticalMultiplier = */ 1,
//                        /* rightSpace = */ 0,
//                        /* rotation = */ Printer.ROTATION_90_DEGREES,
//                        /* reverse = */ false,
//                        /* bold = */ true,
//                        /* alignment = */ Printer.TEXT_ALIGNMENT_LEFT
//                    );
//                    printer.print(1, 1);

//                    printer.drawQrCode(
//                        "QR Code Test",
//                        100,
//                        150,
//                        Printer.QR_CODE_MODEL1,
//                        Printer.ECC_LEVEL_15,
//                        1,
//                        Printer.ROTATION_NONE
//                    );
//                    printer.print(1, 1);

//                    val sbplCommand = """
//    [ESC]A
//    [ESC]H0100
//    [ESC]V0100
//    [ESC]L0202
//    [ESC]XU
//    Hello SATO
//    [ESC]Q1
//    [ESC]Z
//""".trimIndent().replace("[ESC]", "\u001B")
//                    val field = Printer::class.java.getDeclaredField("mServiceManager")
//                    field.isAccessible = true
//                    val serviceManager = field.get(printer) as ServiceManager
//                    serviceManager.inputPrintJob(sbplCommand.toByteArray())
//                    printer.print(1, 1)
//                } else {
//                    onStatus("Connection failed.")
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

    fun sendRawToSato1(ip: String, port: Int = 9100, command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 5000)

                val output = socket.getOutputStream()
                val writer = OutputStreamWriter(output, Charsets.ISO_8859_1)
                val buffered = BufferedWriter(writer)

                // Verified working SBPL with proper termination
//                val sbpl = buildString {
//                    append("\u001B@\r\n")           // Initialize printer
//                    append("\u001BA\r\n")           // Start of label
//                    append("\u001BH0100\r\n")       // Horizontal pos
//                    append("\u001BV0100\r\n")       // Vertical pos
//                    append("\u001BXU\r\n")          // Set font
//                    append("\u001BFHello")            // Print text
//                    append("\u001BQ1\r\n")          // Quantity 1
//                    append("\u001BZ\r\n")           // End and print
//                }

//                buffered.write(sbpl)
                buffered.flush()

                // Optional: wait before closing socket
                delay(500)

                buffered.close()
                output.close()
                socket.close()

                Log.d("SBPL", "✅ Command sent successfully")

            } catch (e: Exception) {
                Log.e("RawPrint", "❌ Print failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}

