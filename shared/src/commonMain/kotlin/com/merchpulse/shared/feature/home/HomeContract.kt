package com.merchpulse.shared.feature.home

import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

data class HomeState(
    val employeeName: String = "",
    val lowStockCount: Int = 0,
    val upcomingProductsCount: Int = 0,
    val todayPunchesCount: Int = 0,
    val arrivalsCount: Int = 0,
    val activeEmployeesCount: Int = 0,
    val totalEmployeesCount: Int = 0,
    val todayPunches: List<TimePunch> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class HomeIntent : UiIntent {
    data object LoadDashboard : HomeIntent()
}

sealed class HomeEffect : UiEffect {
    data class ShowMessage(val message: String) : HomeEffect()
}
