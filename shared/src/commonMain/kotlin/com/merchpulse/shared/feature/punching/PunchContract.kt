package com.merchpulse.shared.feature.punching

import com.merchpulse.shared.domain.model.PunchType
import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

// ─── My Punch (Self) ────────────────────────────────────────

data class PunchState(
    val lastPunch: TimePunch? = null,
    val todayPunches: List<TimePunch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val firstIn: Instant? = null,
    val lastOut: Instant? = null,
    val employeeName: String = "",
    val employeeRole: String = "",
    val shiftDuration: String = "0h 0m",
    val estimatedEarnings: String = "$0.00"
) : UiState

sealed class PunchIntent : UiIntent {
    data object LoadStatus : PunchIntent()
    data class RecordPunch(val type: PunchType, val note: String? = null) : PunchIntent()
}

sealed class PunchEffect : UiEffect {
    data class ShowMessage(val message: String) : PunchEffect()
}

// ─── Team Punches (Manager/Admin) ───────────────────────────

data class DailySummary(
    val employeeId: String,
    val employeeName: String,
    val firstIn: Instant?,
    val lastOut: Instant?,
    val totalPunches: Int
)

data class TeamPunchState(
    val summaries: List<DailySummary> = emptyList(),
    val allPunches: List<TimePunch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDate: LocalDate? = null
) : UiState

sealed class TeamPunchIntent : UiIntent {
    data object LoadToday : TeamPunchIntent()
    data class LoadForDate(val date: LocalDate) : TeamPunchIntent()
    data class CorrectPunch(
        val punchId: String,
        val newTimestamp: Instant,
        val newType: PunchType,
        val reason: String
    ) : TeamPunchIntent()
}

sealed class TeamPunchEffect : UiEffect {
    data class ShowMessage(val message: String) : TeamPunchEffect()
}
