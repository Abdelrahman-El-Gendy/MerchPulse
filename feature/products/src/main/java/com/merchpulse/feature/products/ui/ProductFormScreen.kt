package com.merchpulse.feature.products.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.feature.products.presentation.ProductFormViewModel
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.feature.products.AuditReason
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

    // Colors
    val darkBg = Color(0xFF0D121F)
    val cardBg = Color(0xFF1E2538)
    val accentBlue = Color(0xFF3B82F6)
    val warningBg = Color(0xFF38251E)
    val warningText = Color(0xFFF59E0B)
    val greyText = Color(0xFF94A3B8)

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
        containerColor = darkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBg),
                title = {
                    Column {
                        Text(
                            if (state.isEditing) "Edit Product" else "New Product",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.isEditing) {
                            Text(
                                state.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = greyText
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = darkBg,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.handleIntent(ProductFormIntent.Save) },
                        modifier = Modifier.weight(1.5f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentBlue)
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.isEditing) "Save Changes" else "Create Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Permission Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = warningBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, warningText.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, null, tint = warningText, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Restricted Access", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "You have PRODUCT_EDIT permissions. Sensitive stock logs are read-only.",
                            color = warningText,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Basic Information Section
            FormSection(
                title = "BASIC INFORMATION",
                icon = Icons.Default.Info,
                accentColor = Color(0xFF3B82F6),
                cardBg = cardBg
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(16.dp)) {
                    FormTextField(
                        value = state.name,
                        onValueChange = { viewModel.handleIntent(ProductFormIntent.NameChanged(it)) },
                        label = "Product Name",
                        greyText = greyText
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FormTextField(
                            value = state.sku,
                            onValueChange = { viewModel.handleIntent(ProductFormIntent.SkuChanged(it)) },
                            label = "SKU",
                            modifier = Modifier.weight(1f),
                            greyText = greyText
                        )
                        FormTextField(
                            value = state.price,
                            onValueChange = { viewModel.handleIntent(ProductFormIntent.PriceChanged(it)) },
                            label = "Price",
                            prefix = "$ ",
                            modifier = Modifier.weight(1f),
                            keyboardType = KeyboardType.Decimal,
                            greyText = greyText
                        )
                    }

                    // Category Dropdown (Simplified)
                    Column {
                        Text("Category", style = MaterialTheme.typography.labelMedium, color = greyText)
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(state.category.ifEmpty { "Select Category" }, color = Color.White)
                                Icon(Icons.Default.KeyboardArrowDown, null, tint = greyText)
                            }
                        }
                    }

                    // Status Selector
                    Column {
                        Text("Product Status", style = MaterialTheme.typography.labelMedium, color = greyText)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            ProductStatus.entries.forEach { status ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (state.status == status) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                                        .clickable { viewModel.handleIntent(ProductFormIntent.StatusChanged(status)) }
                                        .border(
                                            width = if (state.status == status) 1.dp else 0.dp,
                                            color = if (state.status == status) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        status.name.lowercase().replaceFirstChar { it.uppercase() },
                                        color = if (state.status == status) accentBlue else greyText,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (state.status == status) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Stock Management Section
            FormSection(
                title = "STOCK MANAGEMENT",
                icon = Icons.Default.Inventory,
                accentColor = Color(0xFF3B82F6), // Using same for consistency
                cardBg = cardBg,
                showLeftAccent = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(16.dp)) {
                    // Current Stock Adjustment
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Current Stock", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Units available", color = greyText, style = MaterialTheme.typography.labelSmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.handleIntent(ProductFormIntent.DecrementStock) },
                                    modifier = Modifier.size(40.dp).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Text(
                                    state.stockQty,
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { viewModel.handleIntent(ProductFormIntent.IncrementStock) },
                                    modifier = Modifier.size(40.dp).background(accentBlue, CircleShape)
                                ) {
                                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    FormTextField(
                        value = state.lowStockThreshold,
                        onValueChange = { viewModel.handleIntent(ProductFormIntent.ThresholdChanged(it)) },
                        label = "Low Stock Threshold",
                        keyboardType = KeyboardType.Number,
                        greyText = greyText,
                        helperText = "Alert triggered when stock falls below this value."
                    )
                    
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    // Audit Log Reason
                    Column {
                        Text("AUDIT LOG REASON", style = MaterialTheme.typography.labelSmall, color = greyText, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AuditReason.entries.forEach { reason ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (state.auditReason == reason) accentBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f))
                                        .clickable { viewModel.handleIntent(ProductFormIntent.AuditReasonChanged(reason)) }
                                        .border(
                                            1.dp, 
                                            if (state.auditReason == reason) accentBlue.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(20.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        reason.name.lowercase().replaceFirstChar { it.uppercase() },
                                        color = if (state.auditReason == reason) accentBlue else greyText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Note field
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Notes, null, tint = greyText, modifier = Modifier.size(20.dp).padding(top = 4.dp))
                                Spacer(Modifier.width(12.dp))
                                TextField(
                                    value = state.adjustmentNote,
                                    onValueChange = { viewModel.handleIntent(ProductFormIntent.AdjustmentNoteChanged(it)) },
                                    placeholder = { Text("Add a note for this adjustment (Required)", color = greyText, style = MaterialTheme.typography.bodyMedium) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    minLines = 3
                                )
                            }
                        }
                    }
                    Text(
                        "* This note will be recorded in the inventory audit trail visible to managers.",
                        style = MaterialTheme.typography.labelSmall,
                        color = greyText.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(Modifier.height(100.dp)) // Padding for bottom bar
        }
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    cardBg: Color,
    showLeftAccent: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        color = cardBg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            if (showLeftAccent) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 350.dp) // Very narrow line on the left
                        .background(accentColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                )
            }
            Column {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).background(accentColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }
                content()
            }
        }
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    greyText: Color,
    helperText: String? = null
) {
    Column(modifier = modifier) {
        // Label using the "floating" style from design
        Text(label, style = MaterialTheme.typography.labelSmall, color = greyText, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (prefix != null) {
                    Text(prefix, color = greyText, style = MaterialTheme.typography.bodyLarge)
                }
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    singleLine = true
                )
            }
        }
        if (helperText != null) {
            Spacer(Modifier.height(4.dp))
            Text(helperText, style = MaterialTheme.typography.labelSmall, color = greyText.copy(alpha = 0.8f))
        }
    }
}
