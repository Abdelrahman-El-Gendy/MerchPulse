package com.merchpulse.feature.punching.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.PunchType
import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.domain.repository.PunchRepository
import com.merchpulse.shared.domain.repository.SessionManager
import com.merchpulse.shared.feature.punching.PunchEffect
import com.merchpulse.shared.feature.punching.PunchIntent
import com.merchpulse.shared.feature.punching.PunchState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.util.UUID
import java.util.Locale

class PunchViewModel(
    private val repository: PunchRepository,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(PunchState())
    val state = _state.asStateFlow()

    private val _effect = Channel<PunchEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(PunchIntent.LoadStatus)
        startDurationTimer()
    }

    private fun startDurationTimer() {
        viewModelScope.launch {
            while (true) {
                delay(60000) // Update every minute
                updateDurationAndEarnings()
            }
        }
    }

    fun handleIntent(intent: PunchIntent) {
        viewModelScope.launch(dispatcherProvider.io) {
            when (intent) {
                is PunchIntent.LoadStatus -> loadStatus()
                is PunchIntent.RecordPunch -> recordPunch(intent)
            }
        }
    }

    private suspend fun loadStatus() {
        val employee = sessionManager.currentEmployee.value ?: return
        
        combine(
            repository.getLastPunch(employee.id),
            getTodayPunchesFlow(employee.id)
        ) { last, todayPunches ->
            val sins = todayPunches.filter { it.type == PunchType.IN }.sortedBy { it.timestamp }
            val outs = todayPunches.filter { it.type == PunchType.OUT }.sortedBy { it.timestamp }
            
            _state.update {
                it.copy(
                    lastPunch = last,
                    todayPunches = todayPunches.sortedByDescending { p -> p.timestamp },
                    firstIn = sins.firstOrNull()?.timestamp,
                    lastOut = todayPunches.filter { p -> p.type == PunchType.OUT }.maxByOrNull { p -> p.timestamp }?.timestamp,
                    employeeName = employee.fullName,
                    employeeRole = employee.role.name.lowercase().capitalize() // fallback
                )
            }
            updateDurationAndEarnings()
        }.collect()
    }

    private fun getTodayPunchesFlow(employeeId: String): Flow<List<TimePunch>> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfDay = LocalDateTime(today, LocalTime(0, 0)).toInstant(TimeZone.currentSystemDefault())
        val endOfDay = LocalDateTime(today, LocalTime(23, 59, 59)).toInstant(TimeZone.currentSystemDefault())
        return repository.getPunchesForEmployee(employeeId, startOfDay, endOfDay)
    }

    private fun updateDurationAndEarnings() {
        val punches = _state.value.todayPunches.sortedBy { it.timestamp }
        if (punches.isEmpty()) return

        val firstIn = punches.firstOrNull { it.type == PunchType.IN }?.timestamp ?: return
        val isCurrentlyIn = _state.value.lastPunch?.type == PunchType.IN
        
        // Use current time if clocked in, otherwise use the very last OUT punch
        val endTime = if (isCurrentlyIn) {
            Clock.System.now()
        } else {
            punches.lastOrNull { it.type == PunchType.OUT }?.timestamp ?: firstIn
        }
        
        val totalMillis = if (endTime > firstIn) {
            (endTime - firstIn).inWholeMilliseconds
        } else 0L

        val hours = totalMillis / 3600000
        val minutes = (totalMillis % 3600000) / 60000
        
        val hourlyRate = 20.50 // Mock rate
        val earnings = (totalMillis.toDouble() / 3600000.0) * hourlyRate

        _state.update {
            it.copy(
                shiftDuration = "${hours}h ${minutes}m",
                estimatedEarnings = "$${String.format("%.2f", earnings)}"
            )
        }
    }

    private suspend fun recordPunch(intent: PunchIntent.RecordPunch) {
        val employee = sessionManager.currentEmployee.value ?: return
        
        // Basic validation: Prevent consecutive IN or OUT
        val last = _state.value.lastPunch
        if (last != null && last.type == intent.type) {
            _effect.send(PunchEffect.ShowMessage("Already ${intent.type}"))
            return
        }

        val punch = TimePunch(
            id = UUID.randomUUID().toString(),
            employeeId = employee.id,
            timestamp = Clock.System.now(),
            type = intent.type,
            deviceId = null,
            note = intent.note,
            createdBy = employee.id
        )

        val result = repository.recordPunch(punch)
        if (result.isSuccess) {
            _effect.send(PunchEffect.ShowMessage("Punched ${intent.type} successfully"))
            // State will update via flow collection in loadStatus
        } else {
            _effect.send(PunchEffect.ShowMessage("Punch failed"))
        }
    }
}
