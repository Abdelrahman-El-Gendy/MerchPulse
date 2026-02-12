package com.merchpulse.feature.stock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merchpulse.feature.stock.presentation.LowStockViewModel
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.feature.stock.StockIntent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(
    viewModel: LowStockViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Low Stock Alerts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.lowStockProducts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.lowStockProducts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No low stock alerts today!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(state.lowStockProducts) { product ->
                    LowStockItem(
                        product = product,
                        onAdjust = { qty -> 
                            viewModel.handleIntent(StockIntent.AdjustStock(product.id, qty))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LowStockItem(
    product: Product,
    onAdjust: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var newQty by remember { mutableStateOf(product.stockQty.toString()) }

    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("Current Stock: ${product.stockQty}", color = MaterialTheme.colorScheme.error)
                Text("Threshold: ${product.lowStockThreshold}", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { showDialog = true }) {
                Text("Adjust")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Adjust Stock") },
            text = {
                OutlinedTextField(
                    value = newQty,
                    onValueChange = { newQty = it },
                    label = { Text("New Quantity") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onAdjust(newQty.toIntOrNull() ?: product.stockQty)
                    showDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
