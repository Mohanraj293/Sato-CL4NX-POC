package com.lazymohan.satopoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.lazymohan.satopoc.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var satoCL4NXPrinterHelper: SatoCL4NXPrinterHelper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        satoCL4NXPrinterHelper = SatoCL4NXPrinterHelper(this) { status ->
            println(status)
        }
        binding.btnAvailPrinters.setOnClickListener {
            satoCL4NXPrinterHelper.getNetworkPrinters()
        }
        binding.btnConnect.setOnClickListener {
            satoCL4NXPrinterHelper.connectAndPrint("192.168.3.180", 9100, 10000)
        }
        binding.btnDisconnect.setOnClickListener {
            satoCL4NXPrinterHelper.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        satoCL4NXPrinterHelper.disconnect()
    }
}