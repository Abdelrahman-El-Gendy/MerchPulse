package com.merchpulse.feature.home.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.merchpulse.feature.home.presentation.HomeViewModel
import org.koin.androidx.compose.koinViewModel

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
    
    // Colors
    val darkBg = Color(0xFF0D121F)
    val cardBg = Color(0xFF1E2538)
    val accentBlue = Color(0xFF3B82F6)
    val statusGreen = Color(0xFF10B981)
    val statusRed = Color(0xFFEF4444)

    Scaffold(
        containerColor = darkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Action */ },
                containerColor = accentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 100.dp) 
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
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
                        color = Color.Gray
                    )
                    Text(
                        "Hello, Admin \uD83D\uDC4B",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box {
                    IconButton(
                        onClick = { /* Notifications */ },
                        modifier = Modifier
                            .background(cardBg, CircleShape)
                            .size(48.dp)
                    ) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = Color.White)
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

            Spacer(Modifier.height(32.dp))

            // Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Warning,
                    iconBg = Color(0x33EF4444),
                    iconTint = statusRed,
                    badgeText = "Critical",
                    badgeBg = Color(0x33EF4444),
                    badgeTint = statusRed,
                    value = state.lowStockCount.toString(),
                    label = "Low Stock Items",
                    cardBg = cardBg,
                    onClick = onNavigateToLowStock
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Inventory,
                    iconBg = Color(0x333B82F6),
                    iconTint = accentBlue,
                    badgeText = "New",
                    badgeBg = Color(0x333B82F6),
                    badgeTint = accentBlue,
                    value = state.arrivalsCount.toString(),
                    label = "Arrivals Today",
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
                    "Daily Punch Summary",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToTeamPunches) {
                    Text("View All", color = accentBlue)
                }
            }

            Spacer(Modifier.height(16.dp))

            val mockEmployees = listOf(
                EmployeePunchMock("John Doe", "Inventory Manager", "08:00 AM", "In Shift", true),
                EmployeePunchMock("Jane Smith", "Sales Associate", "05:00 PM", "Clocked Out", false),
                EmployeePunchMock("Robert Fox", "Technician", "09:00 AM", "In Shift", true),
                EmployeePunchMock("Michael Chen", "Logistics", "--:--", "Not Started", false)
            )

            mockEmployees.forEach { emp ->
                EmployeePunchRow(emp, cardBg, statusGreen, statusRed)
                Spacer(Modifier.height(12.dp))
            }
            
            Spacer(Modifier.height(120.dp))
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
            Text(value, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
                        colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Punches Today", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(active.toString(), style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(" / $total", style = MaterialTheme.typography.headlineSmall, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Text("Active Employees", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                }
                Box(
                    modifier = Modifier.size(56.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.People, null, tint = Color.White, modifier = Modifier.size(28.dp))
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(if (emp.isIn) statusGreen else Color.Gray, CircleShape)
                        .border(1.5.dp, cardBg, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(emp.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(emp.role, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (emp.isIn) Icons.Default.Login else Icons.Default.Logout,
                        null,
                        tint = if (emp.isIn) statusGreen else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(emp.time, color = Color.White, style = MaterialTheme.typography.titleSmall)
                }
                Text(emp.status, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
