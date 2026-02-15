package com.merchpulse.feature.products.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.core.designsystem.R
import com.merchpulse.feature.products.presentation.ProductViewModel
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.feature.products.ProductsIntent
import org.koin.androidx.compose.koinViewModel

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@Composable
fun InventoryFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    isCritical: Boolean = false
) {
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) selectedColor else cardBg,
        shape = RoundedCornerShape(24.dp),
        border = if (isCritical && !selected) BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
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
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when(product.category) {
                        "Electronics" -> Icons.Default.Devices
                        "Clothing" -> Icons.Default.Checkroom
                        else -> Icons.Default.Inventory2
                    },
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            stringResource(R.string.sku_label, product.sku),
                            style = MaterialTheme.typography.labelSmall,
                            color = greyText
                        )
                    }
                    
                    // Status Badge
                    val (statusLabel, statusColor) = when {
                        isLowStock -> stringResource(R.string.low_stock) to statusRed
                        product.status == ProductStatus.UPCOMING -> stringResource(R.string.upcoming) to statusBlue
                        else -> stringResource(R.string.in_stock) to statusGreen
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Column(horizontalAlignment = Alignment.End) {
                        val stockInfo = if (isLowStock) {
                            stringResource(R.string.stock_left_label, product.stockQty)
                        } else if (product.status == ProductStatus.UPCOMING) {
                            stringResource(R.string.avail_date)
                        } else {
                            stringResource(R.string.units_label, product.stockQty)
                        }
                        
                        Text(
                            stockInfo,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLowStock) statusRed else greyText
                        )
                        
                        if (isLowStock) {
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (product.lowStockThreshold > 0) product.stockQty.toFloat() / (product.lowStockThreshold * 4) else 1f)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
    
    // Colors
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val accentBlue = MaterialTheme.colorScheme.primary
    val statusGreen = Color(0xFF10B981)
    val statusRed = MaterialTheme.colorScheme.error
    val statusBlue = MaterialTheme.colorScheme.secondary
    val greyText = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = darkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (!isExpanded) {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    containerColor = accentBlue,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 100.dp) // Avoid overlap with bottom bar
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_product))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1400.dp)
                    .fillMaxSize()
                    .padding(horizontal = if (isExpanded) 48.dp else 24.dp)
            ) {
            Spacer(Modifier.height(56.dp)) // StatusBar space

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.inventory),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExpanded) {
                        Button(
                            onClick = onNavigateToAdd,
                            modifier = Modifier.padding(end = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_product))
                        }
                    }
                    IconButton(
                        onClick = { /* Notifications */ },
                        modifier = Modifier
                            .background(cardBg, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Notifications, stringResource(R.string.notifications), tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Search Bar & Filters (Could be a Row in Expanded)
            if (isExpanded || isMedium) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.handleIntent(ProductsIntent.SearchProducts(it)) },
                        placeholder = { Text(stringResource(R.string.search_placeholder), color = greyText) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            disabledContainerColor = cardBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = greyText) },
                        singleLine = true
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        item {
                            InventoryFilterChip(
                                label = stringResource(R.string.all),
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
                                label = stringResource(R.string.active),
                                selected = state.statusFilter == ProductStatus.ACTIVE,
                                onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.ACTIVE)) },
                                selectedColor = accentBlue
                            )
                        }
                        item {
                            InventoryFilterChip(
                                label = stringResource(R.string.upcoming),
                                selected = state.statusFilter == ProductStatus.UPCOMING,
                                onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.UPCOMING)) },
                                selectedColor = accentBlue
                            )
                        }
                        item {
                            InventoryFilterChip(
                                label = stringResource(R.string.low_stock),
                                selected = state.showLowStockOnly,
                                onClick = { viewModel.handleIntent(ProductsIntent.ToggleLowStockFilter(true)) },
                                selectedColor = statusRed,
                                isCritical = true
                            )
                        }
                    }
                }
            } else {
                TextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.handleIntent(ProductsIntent.SearchProducts(it)) },
                    placeholder = { Text(stringResource(R.string.search_placeholder), color = greyText) },
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
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = greyText) },
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        InventoryFilterChip(
                            label = stringResource(R.string.all),
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
                            label = stringResource(R.string.active),
                            selected = state.statusFilter == ProductStatus.ACTIVE,
                            onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.ACTIVE)) },
                            selectedColor = accentBlue
                        )
                    }
                    item {
                        InventoryFilterChip(
                            label = stringResource(R.string.upcoming),
                            selected = state.statusFilter == ProductStatus.UPCOMING,
                            onClick = { viewModel.handleIntent(ProductsIntent.FilterByStatus(ProductStatus.UPCOMING)) },
                            selectedColor = accentBlue
                        )
                    }
                    item {
                        InventoryFilterChip(
                            label = stringResource(R.string.low_stock),
                            selected = state.showLowStockOnly,
                            onClick = { viewModel.handleIntent(ProductsIntent.ToggleLowStockFilter(true)) },
                            selectedColor = statusRed,
                            isCritical = true
                        )
                    }
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
                    stringResource(R.string.items_count, state.products.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = greyText,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Sort Action */ }
                ) {
                    Text(
                        stringResource(R.string.sort_by_newest),
                        style = MaterialTheme.typography.labelLarge,
                        color = accentBlue
                    )
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = accentBlue, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Products List (Grid for wide screens)
            if (state.isLoading && state.products.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = if (isExpanded) GridCells.Fixed(3) else if (isMedium) GridCells.Fixed(2) else GridCells.Fixed(1),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
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
}


