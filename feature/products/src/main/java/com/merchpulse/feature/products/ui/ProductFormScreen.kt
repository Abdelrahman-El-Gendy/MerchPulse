package com.merchpulse.feature.products.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.merchpulse.feature.products.presentation.ProductFormViewModel
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.feature.products.ProductFormEffect
import com.merchpulse.shared.feature.products.ProductFormIntent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: String? = null,
    viewModel: ProductFormViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.handleIntent(ProductFormIntent.LoadProduct(productId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProductFormEffect.NavigateBack -> onNavigateBack()
                is ProductFormEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ProductFormEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Product" else "New Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.sku,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.SkuChanged(it)) },
                label = { Text("SKU") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.NameChanged(it)) },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.DescriptionChanged(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.category,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.CategoryChanged(it)) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.price,
                    onValueChange = { viewModel.handleIntent(ProductFormIntent.PriceChanged(it)) },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.currency,
                    onValueChange = { /* read only for now */ },
                    label = { Text("Currency") },
                    readOnly = true,
                    modifier = Modifier.weight(0.5f)
                )
            }

            OutlinedTextField(
                value = state.stockQty,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.StockQtyChanged(it)) },
                label = { Text("Stock Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.lowStockThreshold,
                onValueChange = { viewModel.handleIntent(ProductFormIntent.ThresholdChanged(it)) },
                label = { Text("Low Stock Threshold") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Status", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProductStatus.entries.forEach { status ->
                    FilterChip(
                        selected = state.status == status,
                        onClick = { viewModel.handleIntent(ProductFormIntent.StatusChanged(status)) },
                        label = { Text(status.name) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.handleIntent(ProductFormIntent.Save) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (state.isEditing) "Update Product" else "Create Product")
            }
        }
    }
}
