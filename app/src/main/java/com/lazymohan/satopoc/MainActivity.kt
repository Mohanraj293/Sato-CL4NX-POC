package com.lazymohan.satopoc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : ComponentActivity() {

    private lateinit var satoCL4NXPrinterHelper: SatoCL4NXPrinterHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        satoCL4NXPrinterHelper = SatoCL4NXPrinterHelper(this) { status ->
//            println(status)
//        }
//        satoCL4NXPrinterHelper.connectAndPrint("192.168.0.146", 9100, 10000)
        sendRawSBPLToSato("192.168.0.146", 9100)

    }

    override fun onDestroy() {
        super.onDestroy()
//        satoCL4NXPrinterHelper.disconnect()
    }

    fun sendRawSBPLToSato(ip: String, port: Int = 9100) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 5000)

                val output = socket.getOutputStream()
                val writer = OutputStreamWriter(output, Charsets.ISO_8859_1)
                val buffered = BufferedWriter(writer)

                val sbplCommand = """
   SIZE 100 mm, 60 mm
DIRECTION 1
CLS
TEXT 50,50,"3",0,1,1,"SERUM INSTITUTE OF INDIA PVT. LTD."
TEXT 50,100,"3",0,1,1,"CALIBRATION OF NON-CRITICAL INSTRUMENTS"
LINE 50,130,550,130,2
TEXT 50,160,"2",0,1,1,"Inst.: CALIBRATION OF TEMPRATURE & HUMIDITY"
TEXT 50,200,"2",0,1,1,"TRANSMITTER LOOP"
LINE 50,230,550,230,1
TEXT 50,260,"2",0,1,1,"Cal Dt: DD.MM.YYYY    WO No: MW0XXXXXX"
TEXT 50,290,"2",0,1,1,"Asset ID MXXXXX    Due Dt: DD.MM.YYYY"
TEXT 50,320,"2",0,1,1,"Cal By:"
BARCODE 400,320,"128",40,1,0,2,2,"000098765"
PRINT 1
""".trimIndent().replace("[ESC]", "\u001B")

                // Verified working SBPL with proper termination
//                val sbpl = buildString {
//                    append("\u001B@\r\n")           // Initialize printer
//                    append("\u001BA\r\n")           // Start of label
//                    append("\u001BH0100\r\n")       // Horizontal pos
//                    append("\u001BV0100\r\n")       // Vertical pos
//                    append("\u001BXU\r\n")          // Set font
//                    append("\u001BD Hello\r\n")  // Draw text
//                    append("\u001BQ1\r\n")          // Quantity 1
//                    append("\u001BZ\r\n")           // End and print
//                }

                buffered.write(sbplCommand)
                buffered.flush()

                // Optional: wait before closing socket
                delay(500)

                buffered.close()
                output.close()
                socket.close()

                Log.d("SBPL", "✅ Command sent successfully")

            } catch (e: Exception) {
                Log.e("SBPL", "❌ Failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}