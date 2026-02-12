package com.merchpulse.feature.punching.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.merchpulse.feature.punching.presentation.TeamPunchViewModel
import com.merchpulse.shared.feature.punching.TeamPunchIntent
import org.koin.androidx.compose.koinViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Attendance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Date Picker */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.selectedDate?.let { date ->
                Text(
                    "Date: $date",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.summaries) { summary ->
                    ListItem(
                        headlineContent = { Text(summary.employeeName) },
                        supportingContent = {
                            Text(
                                "IN: ${summary.firstIn?.toLocalDateTime(TimeZone.currentSystemDefault())?.time ?: "--:--"} | " +
                                "OUT: ${summary.lastOut?.toLocalDateTime(TimeZone.currentSystemDefault())?.time ?: "--:--"}"
                            )
                        },
                        trailingContent = {
                            Badge { Text("${summary.totalPunches}") }
                        }
                    )
                }
            }
        }
    }
}
