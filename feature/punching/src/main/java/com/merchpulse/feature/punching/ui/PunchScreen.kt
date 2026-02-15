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
import androidx.compose.ui.res.stringResource
import com.merchpulse.core.designsystem.R
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.merchpulse.core.designsystem.theme.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PunchScreen(
    viewModel: PunchViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    scaffoldPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()
    val timeZone = TimeZone.currentSystemDefault()
    val windowSizeClass = LocalWindowSizeClass.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium
    
    // Colors using MaterialTheme
    val accentBlue = MaterialTheme.colorScheme.primary
    val statusGreen = Color(0xFF10B981)

    // Running clock state
    var currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(timeZone)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = Clock.System.now().toLocalDateTime(timeZone)
        }
    }
    
    // Pulse animation logic
    val infiniteTransition = rememberInfiniteTransition()
    val isPunchedIn = state.lastPunch?.type == PunchType.IN
    val buttonColor by animateColorAsState(targetValue = if (isPunchedIn) accentBlue else statusGreen, label = "")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isPunchedIn) 0.1f else 0f,
        targetValue = if (isPunchedIn) 0.4f else 0f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = ""
    )
    val buttonScale by animateFloatAsState(targetValue = if (isPunchedIn) 1.05f else 1f, label = "")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isExpanded) {
                // Two-pane layout for tablets
                Row(
                    modifier = Modifier
                        .widthIn(max = 1400.dp)
                        .fillMaxSize()
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    // Left Pane: Status and Control
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PunchHeader(state)
                        Spacer(Modifier.height(32.dp))
                        DigitalClock(currentTime)
                        Spacer(Modifier.height(48.dp))
                        PunchButton(
                            isPunchedIn = isPunchedIn,
                            buttonColor = buttonColor,
                            pulseAlpha = pulseAlpha,
                            buttonScale = buttonScale,
                            accentBlue = accentBlue,
                            statusGreen = statusGreen,
                            onPunch = {
                                val nextType = if (isPunchedIn) PunchType.OUT else PunchType.IN
                                viewModel.handleIntent(PunchIntent.RecordPunch(nextType))
                            }
                        )
                    }

                    // Right Pane: Log and Stats
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            stringResource(R.string.todays_activity), 
                            style = MaterialTheme.typography.headlineSmall, 
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(24.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Timer,
                                label = stringResource(R.string.shift_duration),
                                value = state.shiftDuration,
                                cardBg = MaterialTheme.colorScheme.surface
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.AttachMoney,
                                label = stringResource(R.string.est_earnings),
                                value = state.estimatedEarnings,
                                cardBg = MaterialTheme.colorScheme.surface
                            )
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.todayPunches) { punch ->
                                ActivityItem(punch, MaterialTheme.colorScheme.surface, accentBlue, statusGreen)
                            }
                            if (state.todayPunches.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(stringResource(R.string.no_activity_recorded), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Mobile Layout
                val scrollState = rememberLazyListState()
                val firstItemOffset = remember { derivedStateOf { 
                    if (scrollState.layoutInfo.visibleItemsInfo.isEmpty()) 0f
                    else -scrollState.layoutInfo.visibleItemsInfo[0].offset.toFloat()
                } }
                val firstItemIndex = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
                val headerAlpha by remember { derivedStateOf {
                    if (firstItemIndex.value > 0) 0f
                    else (1f - (firstItemOffset.value / 600f)).coerceIn(0f, 1f)
                } }

                Box(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize()
                ) {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = scaffoldPadding.calculateBottomPadding() + 24.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .graphicsLayer {
                                        translationY = firstItemOffset.value * 0.4f
                                        alpha = headerAlpha
                                    }
                            ) {
                                PunchHeader(state)
                                Spacer(Modifier.height(16.dp))
                                DigitalClock(currentTime, horizontalAlignment = Alignment.CenterHorizontally)
                                Spacer(Modifier.height(24.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                                    PunchButton(
                                        isPunchedIn = isPunchedIn,
                                        buttonColor = buttonColor,
                                        pulseAlpha = pulseAlpha,
                                        buttonScale = buttonScale,
                                        accentBlue = accentBlue,
                                        statusGreen = statusGreen,
                                        onPunch = {
                                            val nextType = if (isPunchedIn) PunchType.OUT else PunchType.IN
                                            viewModel.handleIntent(PunchIntent.RecordPunch(nextType))
                                        }
                                    )
                                }
                                Spacer(Modifier.height(32.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.Timer,
                                        label = stringResource(R.string.shift_duration),
                                        value = state.shiftDuration,
                                        cardBg = MaterialTheme.colorScheme.surface
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.AttachMoney,
                                        label = stringResource(R.string.est_earnings),
                                        value = state.estimatedEarnings,
                                        cardBg = MaterialTheme.colorScheme.surface
                                    )
                                }
                                Spacer(Modifier.height(32.dp))
                            }
                        }

                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 16.dp
                            ) {
                                Column(modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)) {
                                    Box(modifier = Modifier.align(Alignment.CenterHorizontally).width(40.dp).height(4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape))
                                    Spacer(Modifier.height(24.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(stringResource(R.string.todays_activity), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        TextButton(onClick = {}) { Text(stringResource(R.string.view_full_log), color = accentBlue) }
                                    }
                                }
                            }
                        }

                        items(state.todayPunches) { punch ->
                            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(horizontal = 24.dp)) {
                                ActivityItem(punch, MaterialTheme.colorScheme.surface, accentBlue, statusGreen)
                            }
                            Spacer(Modifier.height(12.dp).background(MaterialTheme.colorScheme.background).fillMaxWidth())
                        }
                        
                        if (state.todayPunches.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(48.dp), contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.no_activity_recorded), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PunchHeader(state: com.merchpulse.shared.feature.punching.PunchState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(state.employeeRole, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(state.employeeName, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape), contentAlignment = Alignment.Center) {
                Text(state.employeeName.take(1), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DigitalClock(currentTime: kotlinx.datetime.LocalDateTime, horizontalAlignment: Alignment.Horizontal = Alignment.Start) {
    Column(horizontalAlignment = horizontalAlignment) {
        Text(
            "${currentTime.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }}, ${currentTime.month.name.lowercase().take(3).replaceFirstChar { it.titlecase(Locale.getDefault()) }} ${currentTime.dayOfMonth}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(formatTime(currentTime), style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Text(if (currentTime.hour >= 12) " PM" else " AM", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
fun PunchButton(
    isPunchedIn: Boolean,
    buttonColor: Color,
    pulseAlpha: Float,
    buttonScale: Float,
    accentBlue: Color,
    statusGreen: Color,
    onPunch: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        if (isPunchedIn) {
            Canvas(modifier = Modifier.size(240.dp)) { drawCircle(color = buttonColor.copy(alpha = pulseAlpha)) }
        }
        repeat(3) { i ->
            Canvas(modifier = Modifier.size(180.dp + (i * 30).dp)) {
                drawCircle(color = accentBlue.copy(alpha = 0.05f), style = Stroke(width = 1.dp.toPx()))
            }
        }
        Surface(
            onClick = onPunch,
            modifier = Modifier.size(160.dp * buttonScale),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(2.dp, buttonColor.copy(alpha = 0.5f)),
            shadowElevation = 8.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Fingerprint, null, tint = buttonColor, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(if (isPunchedIn) stringResource(R.string.punch_out) else stringResource(R.string.punch_in), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(if (isPunchedIn) stringResource(R.string.shift_active) else stringResource(R.string.not_started), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (isPunchedIn) {
            Box(modifier = Modifier.size(165.dp).rotate(-45f)) {
                Box(modifier = Modifier.align(Alignment.TopCenter).size(8.dp).background(statusGreen, CircleShape).shadow(4.dp, CircleShape, ambientColor = statusGreen, spotColor = statusGreen))
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
                Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityItem(punch: TimePunch, cardBg: Color, accentBlue: Color, statusGreen: Color) {
    val isIn = punch.type == PunchType.IN
    val dotColor = if (isIn) statusGreen else accentBlue
    
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            Box(modifier = Modifier.size(12.dp).background(dotColor, CircleShape))
            Box(modifier = Modifier.width(1.dp).height(60.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)))
        }
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = cardBg
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(if (isIn) stringResource(R.string.shift_started) else stringResource(R.string.shift_ended), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(stringResource(R.string.regular_pay_warehouse), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    punch.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).let { 
                        "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')} ${if (it.hour >= 12) "PM" else "AM"}"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatTime(time: kotlinx.datetime.LocalDateTime): String {
    val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
    return "${hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}
