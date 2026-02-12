package com.merchpulse.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.domain.repository.PunchRepository
import com.merchpulse.shared.domain.repository.SessionManager
import com.merchpulse.shared.feature.home.HomeEffect
import com.merchpulse.shared.feature.home.HomeIntent
import com.merchpulse.shared.feature.home.HomeState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val punchRepository: PunchRepository,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadDashboard)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDashboard -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            
            val employee = sessionManager.currentEmployee.first()
            if (employee == null) {
                _state.update { it.copy(isLoading = false, error = "No active session") }
                return@launch
            }

            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val startOfDay = LocalDateTime(today, LocalTime(0, 0)).toInstant(TimeZone.currentSystemDefault())
            val endOfDay = LocalDateTime(today, LocalTime(23, 59, 59)).toInstant(TimeZone.currentSystemDefault())

            combine(
                productRepository.getLowStockCount(),
                productRepository.getUpcomingCount(),
                punchRepository.getTodayPunchCount(startOfDay, endOfDay)
            ) { lowStock, upcoming, todayPunches ->
                _state.update { 
                    it.copy(
                        employeeName = employee.fullName,
                        lowStockCount = lowStock,
                        upcomingProductsCount = upcoming,
                        todayPunchesCount = todayPunches,
                        arrivalsCount = upcoming, // Mocking arrivals with upcoming products for now
                        activeEmployeesCount = todayPunches, // Each punch in usually means one active employee
                        totalEmployeesCount = 10, // Mock
                        isLoading = false
                    )
                }
            }.catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }.collect()
        }
    }
}
