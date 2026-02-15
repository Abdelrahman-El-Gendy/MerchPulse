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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.core.designsystem.R
import com.merchpulse.feature.products.presentation.ProductFormViewModel
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.feature.products.AuditReason
import com.merchpulse.shared.feature.products.ProductFormEffect
import com.merchpulse.shared.feature.products.ProductFormIntent
import org.koin.androidx.compose.koinViewModel

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: String? = null,
    viewModel: ProductFormViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    // Colors
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val accentBlue = MaterialTheme.colorScheme.primary
    val warningBg = MaterialTheme.colorScheme.errorContainer
    val warningText = MaterialTheme.colorScheme.onErrorContainer
    val greyText = MaterialTheme.colorScheme.onSurfaceVariant

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {
                    Column {
                        Text(
                            if (state.isEditing) stringResource(R.string.edit_product) else stringResource(R.string.new_product),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more), tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = darkBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier
                            .widthIn(max = if (isExpanded) 600.dp else if (isMedium) 500.dp else 1200.dp)
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(stringResource(R.string.cancel), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.handleIntent(ProductFormIntent.Save) },
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentBlue, contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (state.isEditing) stringResource(R.string.save_changes) else stringResource(R.string.create_product), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = if (isExpanded) 600.dp else if (isMedium) 500.dp else 1200.dp)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(24.dp))

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
                            Text(stringResource(R.string.restricted_access), color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                stringResource(R.string.restricted_access_desc),
                                color = warningText,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                // Basic Information Section
                FormSection(
                    title = stringResource(R.string.basic_information),
                    icon = Icons.Default.Info,
                    accentColor = MaterialTheme.colorScheme.primary,
                    cardBg = cardBg
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(16.dp)) {
                        FormTextField(
                            value = state.name,
                            onValueChange = { viewModel.handleIntent(ProductFormIntent.NameChanged(it)) },
                            label = stringResource(R.string.product_name),
                            greyText = greyText
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            FormTextField(
                                value = state.sku,
                                onValueChange = { viewModel.handleIntent(ProductFormIntent.SkuChanged(it)) },
                                label = stringResource(R.string.sku),
                                modifier = Modifier.weight(1f),
                                greyText = greyText
                            )
                            FormTextField(
                                value = state.price,
                                onValueChange = { viewModel.handleIntent(ProductFormIntent.PriceChanged(it)) },
                                label = stringResource(R.string.price),
                                prefix = "$ ",
                                modifier = Modifier.weight(1f),
                                keyboardType = KeyboardType.Decimal,
                                greyText = greyText
                            )
                        }

                        // Category Dropdown (Simplified)
                        Column {
                            Text(stringResource(R.string.category), style = MaterialTheme.typography.labelMedium, color = greyText)
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(state.category.ifEmpty { stringResource(R.string.select_category) }, color = MaterialTheme.colorScheme.onSurface)
                                    Icon(Icons.Default.KeyboardArrowDown, null, tint = greyText)
                                }
                            }
                        }

                        // Status Selector
                        Column {
                            Text(stringResource(R.string.product_status), style = MaterialTheme.typography.labelMedium, color = greyText)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ProductStatus.entries.forEach { status ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (state.status == status) MaterialTheme.colorScheme.surface else Color.Transparent)
                                            .clickable { viewModel.handleIntent(ProductFormIntent.StatusChanged(status)) }
                                            .border(
                                                width = if (state.status == status) 1.dp else 0.dp,
                                                color = if (state.status == status) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f) else Color.Transparent,
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
                    title = stringResource(R.string.stock_management),
                    icon = Icons.Default.Inventory,
                    accentColor = MaterialTheme.colorScheme.primary, // Using same for consistency
                    cardBg = cardBg,
                    showLeftAccent = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.padding(16.dp)) {
                        // Current Stock Adjustment
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(stringResource(R.string.current_stock), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                    Text(stringResource(R.string.units_available), color = greyText, style = MaterialTheme.typography.labelSmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.handleIntent(ProductFormIntent.DecrementStock) },
                                        modifier = Modifier.size(40.dp).border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                                    }
                                    Text(
                                        state.stockQty,
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = { viewModel.handleIntent(ProductFormIntent.IncrementStock) },
                                        modifier = Modifier.size(40.dp).background(accentBlue, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }

                        FormTextField(
                            value = state.lowStockThreshold,
                            onValueChange = { viewModel.handleIntent(ProductFormIntent.ThresholdChanged(it)) },
                            label = stringResource(R.string.low_stock_threshold),
                            keyboardType = KeyboardType.Number,
                            greyText = greyText,
                            helperText = stringResource(R.string.threshold_helper)
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        // Audit Log Reason
                        Column {
                            Text(stringResource(R.string.audit_log_reason), style = MaterialTheme.typography.labelSmall, color = greyText, fontWeight = FontWeight.Bold)
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
                                            .background(if (state.auditReason == reason) accentBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                            .clickable { viewModel.handleIntent(ProductFormIntent.AuditReasonChanged(reason)) }
                                            .border(
                                                1.dp, 
                                                if (state.auditReason == reason) accentBlue.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Notes, null, tint = greyText, modifier = Modifier.size(20.dp).padding(top = 4.dp))
                                    Spacer(Modifier.width(12.dp))
                                    TextField(
                                        value = state.adjustmentNote,
                                        onValueChange = { viewModel.handleIntent(ProductFormIntent.AdjustmentNoteChanged(it)) },
                                        placeholder = { Text(stringResource(R.string.add_note_placeholder), color = greyText, style = MaterialTheme.typography.bodyMedium) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        minLines = 3
                                    )
                                }
                            }
                        }
                        Text(
                            stringResource(R.string.audit_note_disclaimer),
                            style = MaterialTheme.typography.labelSmall,
                            color = greyText.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(Modifier.height(100.dp)) // Padding for bottom bar
            }
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
                        .width(4.dp) // Narrow line on the left
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
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
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
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
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
