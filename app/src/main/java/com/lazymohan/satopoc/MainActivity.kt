package com.lazymohan.satopoc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.Manifest.permission
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.lazymohan.satopoc.composable.PrinterScreen
import com.lazymohan.satopoc.manager.SatoPrinterManager
import com.lazymohan.satopoc.ui.theme.SatoCL4NXPOCTheme

class MainActivity : ComponentActivity() {
    private lateinit var printerManager: SatoPrinterManager
    private val viewModel: SatoPrinterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.ACCESS_FINE_LOCATION),
            101
        )
        printerManager = SatoPrinterManager(
            context = this,
            onPrinterConnect = {
                if (it) {
                    runOnUiThread {
                        Toast.makeText(this, "Printer connected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Printer disconnected", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
                }
            }
        )
        setContent {
            SatoCL4NXPOCTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PrinterScreen(
                        printerManager = printerManager,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        printerManager.disconnect()
        super.onDestroy()
    }


}