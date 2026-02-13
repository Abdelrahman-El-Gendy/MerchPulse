package com.merchpulse.feature.punching.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.*
import com.merchpulse.feature.punching.presentation.PunchViewModel
import com.merchpulse.shared.domain.model.PunchType
import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.feature.punching.PunchIntent
import org.koin.androidx.compose.koinViewModel
import kotlinx.datetime.Clock
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PunchScreen(
    viewModel: PunchViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    scaffoldPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()
    val timeZone = TimeZone.currentSystemDefault()
    
    // Background colors
    val darkBg = Color(0xFF0D121F)
    val cardBg = Color(0xFF1E2538)
    val accentBlue = Color(0xFF3B82F6)
    val statusGreen = Color(0xFF10B981)

    // Running clock state
    var currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(timeZone)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = Clock.System.now().toLocalDateTime(timeZone)
        }
    }
    
    // State for pulse animation
    val infiniteTransition = rememberInfiniteTransition()
    val isPunchedIn = state.lastPunch?.type == PunchType.IN
    val buttonColor by animateColorAsState(
        targetValue = if (isPunchedIn) accentBlue else statusGreen,
        animationSpec = tween(500)
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isPunchedIn) 0.1f else 0f,
        targetValue = if (isPunchedIn) 0.4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isPunchedIn) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 100f)
    )

    val scrollState = rememberLazyListState()
    val density = androidx.compose.ui.platform.LocalDensity.current
    
    val firstItemOffset = remember { derivedStateOf { 
        if (scrollState.layoutInfo.visibleItemsInfo.isEmpty()) 0f
        else -scrollState.layoutInfo.visibleItemsInfo[0].offset.toFloat()
    } }
    
    val firstItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    
    // Parallax and fade calculations (for items inside the list)
    val headerAlpha by remember {
        derivedStateOf {
            if (firstItemIndex.value > 0) 0f
            else (1f - (firstItemOffset.value / 600f)).coerceIn(0f, 1f)
        }
    }

    Scaffold(
        containerColor = darkBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = scaffoldPadding.calculateBottomPadding() + 24.dp)
        ) {
            // 1. Background Content (now as Items for interactivity)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .graphicsLayer {
                            // Achieve parallax by counter-scrolling slightly
                            translationY = firstItemOffset.value * 0.4f
                            alpha = headerAlpha
                        }
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                state.employeeRole,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray
                            )
                            Text(
                                state.employeeName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Notifications, "Notifications", tint = Color.White.copy(alpha = 0.7f))
                            }
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(state.employeeName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Date and Digital Clock
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${currentTime.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }}, ${currentTime.month.name.lowercase().take(3).replaceFirstChar { it.titlecase(Locale.getDefault()) }} ${currentTime.dayOfMonth}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                formatTime(currentTime),
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (currentTime.hour >= 12) " PM" else " AM",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Circular Punch Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulse background
                        if (isPunchedIn) {
                            Canvas(modifier = Modifier.size(240.dp)) {
                                drawCircle(color = buttonColor.copy(alpha = pulseAlpha))
                            }
                        }

                        // outer glow rings
                        repeat(3) { i ->
                            Canvas(modifier = Modifier.size(180.dp + (i * 30).dp)) {
                                drawCircle(
                                    color = accentBlue.copy(alpha = 0.05f),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                val nextType = if (isPunchedIn) PunchType.OUT else PunchType.IN
                                viewModel.handleIntent(PunchIntent.RecordPunch(nextType))
                            },
                            modifier = Modifier.size(160.dp * buttonScale),
                            shape = CircleShape,
                            color = darkBg,
                            border = BorderStroke(2.dp, buttonColor.copy(alpha = 0.5f)),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Fingerprint,
                                    null,
                                    tint = buttonColor,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    if (isPunchedIn) "PUNCH OUT" else "PUNCH IN",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (isPunchedIn) "Shift Active" else "Not Started",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        // Active indicator dot on the ring
                        if (isPunchedIn) {
                            Box(
                                modifier = Modifier
                                    .size(165.dp)
                                    .rotate(-45f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .size(8.dp)
                                        .background(statusGreen, CircleShape)
                                        .shadow(4.dp, CircleShape, ambientColor = statusGreen, spotColor = statusGreen)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Timer,
                            label = "SHIFT DURATION",
                            value = state.shiftDuration,
                            cardBg = cardBg
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.AttachMoney,
                            label = "EST. EARNINGS",
                            value = state.estimatedEarnings,
                            cardBg = cardBg
                        )
                    }
                    
                    Spacer(Modifier.height(32.dp))
                }
            }

            // 2. The Sliding Sheet Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = darkBg,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                    ) {
                        // Handle bar for visual cue
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Today's Activity", 
                                style = MaterialTheme.typography.titleLarge, 
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = {}) {
                                Text("View Full Log", color = accentBlue)
                            }
                        }
                    }
                }
            }

            // 3. Activity Items (on the sheet)
            items(state.todayPunches) { punch ->
                Box(modifier = Modifier.background(darkBg).padding(horizontal = 24.dp)) {
                    ActivityItem(punch, cardBg, accentBlue, statusGreen)
                }
                Spacer(Modifier.height(12.dp).background(darkBg).fillMaxWidth())
            }
            
            // Empty state if no punches
            if (state.todayPunches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(darkBg)
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No activity recorded yet", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    value: String, 
    cardBg: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = cardBg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityItem(punch: TimePunch, cardBg: Color, accentBlue: Color, statusGreen: Color) {
    val isIn = punch.type == PunchType.IN
    val dotColor = if (isIn) statusGreen else accentBlue
    
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        // Timeline dot and line
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(modifier = Modifier.size(12.dp).background(dotColor, CircleShape))
            Box(modifier = Modifier.width(1.dp).height(60.dp).background(Color.Gray.copy(alpha = 0.2f)))
        }
        
        Spacer(Modifier.width(12.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = cardBg
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        if (isIn) "Shift Started" else "Shift Ended",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        "Regular Pay • Warehouse A",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(
                    punch.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).let { 
                        "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')} ${if (it.hour >= 12) "PM" else "AM"}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

private fun formatTime(time: kotlinx.datetime.LocalDateTime): String {
    val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
    return "${hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}
