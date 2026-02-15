package com.merchpulse.feature.punching.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.PunchType
import com.merchpulse.shared.domain.policy.AuthorizationPolicy
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.PunchRepository
import com.merchpulse.shared.feature.punching.DailySummary
import com.merchpulse.shared.feature.punching.TeamPunchEffect
import com.merchpulse.shared.feature.punching.TeamPunchIntent
import com.merchpulse.shared.feature.punching.TeamPunchState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class TeamPunchViewModel(
    private val employeeRepository: EmployeeRepository,
    private val punchRepository: PunchRepository,
    private val auditRepository: com.merchpulse.shared.domain.repository.AuditRepository,
    private val authPolicy: AuthorizationPolicy,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(TeamPunchState())
    val state = _state.asStateFlow()

    private val _effect = Channel<TeamPunchEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: TeamPunchIntent) {
        when (intent) {
            is TeamPunchIntent.LoadToday -> loadForDate(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
            is TeamPunchIntent.LoadForDate -> loadForDate(intent.date)
            is TeamPunchIntent.CorrectPunch -> correctPunch(intent)
        }
    }

    private fun loadForDate(date: LocalDate) {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true, selectedDate = date) }
            try {
                authPolicy.requirePermission(Permission.PUNCH_VIEW_ALL)
                
                val startOfDay = LocalDateTime(date, LocalTime(0, 0)).toInstant(TimeZone.currentSystemDefault())
                val endOfDay = LocalDateTime(date, LocalTime(23, 59, 59)).toInstant(TimeZone.currentSystemDefault())

                val employees = employeeRepository.getAllEmployees().first()
                punchRepository.getAllPunches(startOfDay, endOfDay).collect { punches ->
                    val summaries = employees.map { emp ->
                        val empPunches = punches.filter { it.employeeId == emp.id }.sortedBy { it.timestamp }
                        val sins = empPunches.filter { it.type == PunchType.IN }
                        val outs = empPunches.filter { it.type == PunchType.OUT }

                        DailySummary(
                            employeeId = emp.id,
                            employeeName = emp.fullName,
                            firstIn = sins.firstOrNull()?.timestamp,
                            lastOut = outs.lastOrNull()?.timestamp,
                            totalPunches = empPunches.size,
                            role = emp.role,
                            isActive = emp.isActive
                        )
                    }
                    _state.update { it.copy(summaries = summaries, allPunches = punches, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun correctPunch(intent: TeamPunchIntent.CorrectPunch) {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                authPolicy.requirePermission(Permission.PUNCH_ADJUST)
                val punch = _state.value.allPunches.find { it.id == intent.punchId } ?: return@launch
                
                val updated = punch.copy(
                    timestamp = intent.newTimestamp,
                    type = intent.newType,
                    note = intent.reason
                )
                
                repositoryUpdate(updated)
                auditRepository.logAction(
                    action = "PUNCH_CORRECTION",
                    entityType = "PUNCH",
                    entityId = punch.id,
                    previousState = punch.toString(),
                    newState = updated.toString(),
                    note = intent.reason
                )
                _effect.send(TeamPunchEffect.ShowMessage("Punch corrected"))
            } catch (e: Exception) {
                _effect.send(TeamPunchEffect.ShowMessage(e.message ?: "Correction failed"))
            }
        }
    }

    private suspend fun repositoryUpdate(punch: com.merchpulse.shared.domain.model.TimePunch) {
        // Implementation detail: use a specific method or the general update
        punchRepository.updatePunch(punch)
    }
}
