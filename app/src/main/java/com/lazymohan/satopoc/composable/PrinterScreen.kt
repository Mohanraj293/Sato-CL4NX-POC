package com.lazymohan.satopoc.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazymohan.satopoc.PrinterUiState
import com.lazymohan.satopoc.SatoPrinterEvents
import com.lazymohan.satopoc.manager.SatoPrinterManager
import com.lazymohan.satopoc.ui.theme.SatoCL4NXPOCTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterScreen(
    modifier: Modifier = Modifier,
    handleEvents: (SatoPrinterEvents) -> Unit,
    uiState: PrinterUiState
) {
    val printers by remember { derivedStateOf { uiState.printers } }
    val snackBarHostState = remember { SnackbarHostState() }

    if (uiState.message?.isNotEmpty() == true) {
        LaunchedEffect(uiState.message) {
            snackBarHostState.showSnackbar(
                message = uiState.message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sato Printer") },
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Network Printers",
                    fontSize = 24.sp,
                    modifier = modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { handleEvents(SatoPrinterEvents.SearchPrinters) },
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text("Scan for Printers")
                }

                Spacer(modifier = modifier.height(16.dp))
                Text(uiState.status, fontSize = 24.sp, modifier = modifier.padding(bottom = 8.dp))
                Spacer(modifier = modifier.height(16.dp))

                if (printers.isEmpty()) {
                    Text("No printers found.", color = Color.Gray)
                } else {
                    LazyColumn {
                        items(printers) { ip ->
                            Card(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { handleEvents(SatoPrinterEvents.ConnectPrinter(ip)) },
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Text(
                                    text = ip,
                                    modifier = modifier.padding(16.dp),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PrintControls(printerManager: SatoPrinterManager) {
        var textToPrint by remember { mutableStateOf("Test Print") }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textToPrint,
                onValueChange = { textToPrint = it },
                label = { Text("Text to print") },
                modifier = modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // Implement print functionality
                    printerManager.printText(textToPrint)
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Print Text")
            }

            Button(
                onClick = {
                    printerManager.printBarcode("123456789012")
                },
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Print Barcode")
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPrintScreen() {
    SatoCL4NXPOCTheme(true) {
        PrinterScreen(
            uiState = PrinterUiState(),
            handleEvents = {}
        )
    }
}

@Preview
@Composable
private fun PreviewPrintScreen1() {
    SatoCL4NXPOCTheme(true) {
        PrinterScreen(
            uiState = PrinterUiState(
                printers = listOf(
                    "Printer - 01",
                    "Printer - 02",
                ),
                status = "Connected",
            ),
            handleEvents = {}
        )
    }
}