package com.merchpulse.feature.products.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.feature.products.presentation.ProductViewModel
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.feature.products.ProductsIntent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    // Colors
    val darkBg = Color(0xFF0D121F)
    val cardBg = Color(0xFF1E2538)
    val accentBlue = Color(0xFF3B82F6)
    val statusGreen = Color(0xFF10B981)
    val statusRed = Color(0xFFEF4444)
    val statusBlue = Color(0xFF3B82F6)
    val greyText = Color(0xFF94A3B8)

    Scaffold(
        containerColor = darkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = accentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 100.dp) // Avoid overlap with bottom bar
            ) {
                Icon(Icons.Default.Add, "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp)) // StatusBar space

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Inventory",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { /* Notifications */ },
                    modifier = Modifier
                        .background(cardBg, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Notifications, "Notifications", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Search Bar
            TextField(
                value = state.searchQuery,
                onValueChange = { viewModel.handleIntent(ProductsIntent.SearchProducts(it)) },
                placeholder = { Text("Search by name or SKU...", color = greyText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    disabledContainerColor = cardBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = greyText) },
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    InventoryFilterChip(
                        label = "All",
                        selected = !state.showLowStockOnly && state.statusFilter == null,
                        onClick = {
                            viewModel.handleIntent(ProductsIntent.ToggleLowStockFilter(false))
                            viewModel.handleIntent(ProductsIntent.FilterByStatus(null))
                        },
                        selectedColor = accentBlue
                    )
                }
                item {
                    InventoryFilterChip(
                        label = "Active",
                        selected = state.statusFilter == ProductStatus.ACTIVE,
                        onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.ACTIVE)) },
                        selectedColor = accentBlue
                    )
                }
                item {
                    InventoryFilterChip(
                        label = "Upcoming",
                        selected = state.statusFilter == ProductStatus.UPCOMING,
                        onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.UPCOMING)) },
                        selectedColor = accentBlue
                    )
                }
                item {
                    InventoryFilterChip(
                        label = "Low Stock",
                        selected = state.showLowStockOnly,
                        onClick = { viewModel.handleIntent(ProductsIntent.ToggleLowStockFilter(true)) },
                        selectedColor = statusRed,
                        isCritical = true
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Stats & Sort
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${state.products.size} ITEMS",
                    style = MaterialTheme.typography.labelLarge,
                    color = greyText,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Sort Action */ }
                ) {
                    Text(
                        "Sort by: Newest",
                        style = MaterialTheme.typography.labelLarge,
                        color = accentBlue
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = accentBlue, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Products List
            if (state.isLoading && state.products.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(state.products) { product ->
                        InventoryProductCard(
                            product = product,
                            cardBg = cardBg,
                            statusGreen = statusGreen,
                            statusRed = statusRed,
                            statusBlue = statusBlue,
                            greyText = greyText,
                            onClick = { onNavigateToDetail(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    isCritical: Boolean = false
) {
    val darkBg = Color(0xFF0D121F)
    val cardBg = Color(0xFF1E2538)
    
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) selectedColor else cardBg,
        shape = RoundedCornerShape(24.dp),
        border = if (isCritical && !selected) BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else if (isCritical) Color(0xFFEF4444) else Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InventoryProductCard(
    product: Product,
    cardBg: Color,
    statusGreen: Color,
    statusRed: Color,
    statusBlue: Color,
    greyText: Color,
    onClick: () -> Unit
) {
    val isLowStock = product.stockQty <= product.lowStockThreshold
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = cardBg,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Accent line for low stock
            if (isLowStock) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(statusRed)
                )
                Spacer(Modifier.width(8.dp))
            }

            // Image Placeholder/Thumb
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Mocking image with icon
                Icon(
                    when(product.category) {
                        "Electronics" -> Icons.Default.Devices
                        "Clothing" -> Icons.Default.Checkroom
                        else -> Icons.Default.Inventory2
                    },
                    null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "SKU: ${product.sku}",
                            style = MaterialTheme.typography.labelSmall,
                            color = greyText
                        )
                    }
                    
                    // Status Badge
                    val (statusLabel, statusColor) = when {
                        isLowStock -> "Low Stock" to statusRed
                        product.status == ProductStatus.UPCOMING -> "Upcoming" to statusBlue
                        else -> "In Stock" to statusGreen
                    }
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            statusLabel,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "${product.currency} ${product.price}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Column(horizontalAlignment = Alignment.End) {
                        val stockInfo = if (isLowStock) {
                            "${product.stockQty}/${product.lowStockThreshold * 4} left" // Mocking capacity
                        } else if (product.status == ProductStatus.UPCOMING) {
                            "Avail. 03/25"
                        } else {
                            "${product.stockQty} units"
                        }
                        
                        Text(
                            stockInfo,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLowStock) statusRed else greyText
                        )
                        
                        if (isLowStock) {
                            Spacer(Modifier.height(8.dp))
                            // Simple Progress Bar
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(product.stockQty.toFloat() / (product.lowStockThreshold * 4))
                                        .fillMaxHeight()
                                        .background(statusRed)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
