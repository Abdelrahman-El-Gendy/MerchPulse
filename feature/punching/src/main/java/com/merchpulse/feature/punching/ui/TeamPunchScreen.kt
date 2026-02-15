package com.merchpulse.feature.punching.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merchpulse.core.designsystem.R
import com.merchpulse.feature.punching.presentation.TeamPunchViewModel
import com.merchpulse.shared.feature.punching.DailySummary
import com.merchpulse.shared.feature.punching.TeamPunchIntent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.merchpulse.shared.domain.model.Role
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamPunchScreen(
    viewModel: TeamPunchViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.handleIntent(TeamPunchIntent.LoadToday)
    }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = state.selectedDate == today
    
    // Formatting date for display: "TODAY, OCT 30"
    // Note: KMP doesn't have java.time.format.DateTimeFormatter directly in commonMain, 
    // but since this file is in androidMain (implied by package structure and Context usage previously), we use Java time or simple string manipulation.
    // For simplicity and robustness in KMP context, let's use a simple helper or just basic string construction if possible.
    // Assuming Android target for now based on imports.
    val month = state.selectedDate?.month?.name?.take(3)?.lowercase()?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: ""
    val day = state.selectedDate?.dayOfMonth ?: ""
    val dateHeader = if (isToday) stringResource(R.string.today_date_format, "$month $day").uppercase() else stringResource(R.string.yesterday_date_format, "$month $day").uppercase()


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.team_punch_history),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter Logic */ }) {
                        Icon(
                            Icons.Default.FilterList, 
                            contentDescription = stringResource(R.string.filter),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = "", // TODO: Implement search state
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_employee_name)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date Picker Card
            OutlinedCard(
                onClick = { /* Open Date Picker */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = BorderStroke(0.dp, Color.Transparent) 
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            // Placeholder date range logic
                            "Oct 24 - Oct 30", 
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        Icons.Default.KeyboardArrowDown, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date Header
            Text(
                text = dateHeader,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.summaries) { summary ->
                        TeamMemberCard(summary = summary)
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberCard(summary: DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) 
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar, Name, Role, Active Status, Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Placeholder
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Could use first letter of name or an icon
                        Text(
                            text = summary.employeeName.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = summary.employeeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (summary.isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF4CAF50).copy(alpha = 0.2f), // Green tint
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.active_label),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                    Text(
                        text = summary.role?.name?.replace("_", " ")?.lowercase()?.capitalize(Locale.getDefault()) ?: "Staff", // Basic formatting
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.options),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Cards Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // First In Card
                TimeStatusCard(
                    label = stringResource(R.string.first_in_label),
                    time = summary.firstIn?.toLocalDateTime(TimeZone.currentSystemDefault())?.let { 
                        // Format: 08:42 AM
                        val hour = if (it.hour > 12) it.hour - 12 else if (it.hour == 0) 12 else it.hour
                        val amPm = if (it.hour >= 12) "PM" else "AM"
                        "${hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')} $amPm"
                    } ?: "--:--",
                    modifier = Modifier.weight(1f)
                )

                // Last Out Card
                TimeStatusCard(
                    label = stringResource(R.string.last_out_label),
                    time = summary.lastOut?.toLocalDateTime(TimeZone.currentSystemDefault())?.let {
                         val hour = if (it.hour > 12) it.hour - 12 else if (it.hour == 0) 12 else it.hour
                        val amPm = if (it.hour >= 12) "PM" else "AM"
                        "${hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')} $amPm"
                    } ?: stringResource(R.string.still_on_shift), 
                    isItalic = summary.lastOut == null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TimeStatusCard(
    label: String,
    time: String,
    isItalic: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) // Distinct background for time blocks
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = if (isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

