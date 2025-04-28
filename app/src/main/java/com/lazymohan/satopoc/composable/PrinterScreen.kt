package com.lazymohan.satopoc.composable

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazymohan.satopoc.PrinterUiState
import com.lazymohan.satopoc.SatoPrinterEvents

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
}
