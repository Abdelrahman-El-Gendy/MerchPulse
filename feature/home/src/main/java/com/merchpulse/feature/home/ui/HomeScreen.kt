package com.merchpulse.feature.home.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merchpulse.core.designsystem.R
import com.merchpulse.feature.home.presentation.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

data class EmployeePunchMock(
    val name: String,
    val role: String,
    val time: String,
    val status: String,
    val isIn: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToInventory: () -> Unit,
    onNavigateToLowStock: () -> Unit,
    onNavigateToPunch: () -> Unit,
    onNavigateToEmployees: () -> Unit,
    onNavigateToTeamPunches: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    
    // Colors
    // Colors
    val darkBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val accentBlue = MaterialTheme.colorScheme.primary
    val statusGreen = Color(0xFF10B981)
    val statusRed = MaterialTheme.colorScheme.error

    Scaffold(
        containerColor = darkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (!isExpanded) {
                FloatingActionButton(
                    onClick = { /* Add Action */ },
                    containerColor = accentBlue,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 100.dp) 
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.add))
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
                    .verticalScroll(rememberScrollState())
            ) {
            Spacer(Modifier.height(56.dp)) 

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Thursday, Feb 12",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        stringResource(R.string.hello_admin),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExpanded) {
                        Button(
                            onClick = { /* Add Product */ },
                            modifier = Modifier.padding(end = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.add_product))
                        }
                    }
                    Box {
                        IconButton(
                            onClick = { /* Notifications */ },
                            modifier = Modifier
                                .background(cardBg, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(Icons.Default.Notifications, stringResource(R.string.notifications), tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(statusRed, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (isExpanded) {
                // Two-column layout for Wide screens
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    // Left Column: Main Stats & Actions
                    Column(modifier = Modifier.weight(1.5f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Warning,
                                iconBg = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                iconTint = statusRed,
                                badgeText = stringResource(R.string.critical),
                                badgeBg = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                badgeTint = statusRed,
                                value = state.lowStockCount.toString(),
                                label = stringResource(R.string.low_stock_items),
                                cardBg = cardBg,
                                onClick = onNavigateToLowStock
                            )
                            SummaryCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Inventory,
                                iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                iconTint = accentBlue,
                                badgeText = stringResource(R.string.new_label),
                                badgeBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                badgeTint = accentBlue,
                                value = state.arrivalsCount.toString(),
                                label = stringResource(R.string.arrivals_today),
                                cardBg = cardBg,
                                onClick = onNavigateToInventory
                            )
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        ActiveEmployeesCard(
                            active = state.activeEmployeesCount,
                            total = state.totalEmployeesCount,
                            onClick = onNavigateToTeamPunches
                        )
                    }

                    // Right Column: Recent Activity / Punches
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.daily_punch_summary),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        val mockEmployees = getMockEmployees()
                        mockEmployees.forEach { emp ->
                            EmployeePunchRow(emp, cardBg, statusGreen, statusRed)
                            Spacer(Modifier.height(12.dp))
                        }
                        TextButton(onClick = onNavigateToTeamPunches, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.view_all), color = accentBlue)
                        }
                    }
                }
            } else {
                // Single column layout for Mobile (default)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Warning,
                        iconBg = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        iconTint = statusRed,
                        badgeText = stringResource(R.string.critical),
                        badgeBg = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                        badgeTint = statusRed,
                        value = state.lowStockCount.toString(),
                        label = stringResource(R.string.low_stock_items),
                        cardBg = cardBg,
                        onClick = onNavigateToLowStock
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Inventory,
                        iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        iconTint = accentBlue,
                        badgeText = stringResource(R.string.new_label),
                        badgeBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        badgeTint = accentBlue,
                        value = state.arrivalsCount.toString(),
                        label = stringResource(R.string.arrivals_today),
                        cardBg = cardBg,
                        onClick = onNavigateToInventory
                    )
                }

                Spacer(Modifier.height(24.dp))

                ActiveEmployeesCard(
                    active = state.activeEmployeesCount,
                    total = state.totalEmployeesCount,
                    onClick = onNavigateToTeamPunches
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.daily_punch_summary),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToTeamPunches) {
                        Text(stringResource(R.string.view_all), color = accentBlue)
                    }
                }

                Spacer(Modifier.height(16.dp))

                getMockEmployees().forEach { emp ->
                    EmployeePunchRow(emp, cardBg, statusGreen, statusRed)
                    Spacer(Modifier.height(12.dp))
                }
            }
            
            Spacer(Modifier.height(120.dp))
        }
    }
}
}

@Composable
fun SummaryCard(
    modifier: Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    badgeText: String,
    badgeBg: Color,
    badgeTint: Color,
    value: String,
    label: String,
    cardBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = cardBg,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeTint,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActiveEmployeesCard(active: Int, total: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.punches_today), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(active.toString(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Text(" / $total", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Text(stringResource(R.string.active_employees), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
                Box(
                    modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.People, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

@Composable
fun EmployeePunchRow(emp: EmployeePunchMock, cardBg: Color, statusGreen: Color, statusRed: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cardBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Canvas(modifier = Modifier.size(48.dp)) {
                    drawCircle(Color.Gray.copy(alpha = 0.3f))
                }
                Text(
                    emp.name.take(1) + (emp.name.split(" ").getOrNull(1)?.take(1) ?: ""),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(if (emp.isIn) statusGreen else MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
                        .border(1.5.dp, cardBg, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(emp.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(emp.role, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (emp.isIn) Icons.AutoMirrored.Filled.Login else Icons.AutoMirrored.Filled.Logout,
                        null,
                        tint = if (emp.isIn) statusGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(emp.time, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall)
                }
                Text(emp.status, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private fun getMockEmployees() = listOf(
    EmployeePunchMock("Alex Johnson", "Inventory Manager", "08:30 AM", "Active • In Warehouse", true),
    EmployeePunchMock("Maria Garcia", "Sales Associate", "09:15 AM", "Active • Main Floor", true),
    EmployeePunchMock("Ryan Smith", "Logistics", "07:45 AM", "Clocked Out • 5:00 PM", false),
    EmployeePunchMock("Sarah Wilson", "Technician", "10:00 AM", "Active • Service Center", true)
)
