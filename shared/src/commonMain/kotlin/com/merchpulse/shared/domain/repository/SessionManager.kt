package com.merchpulse.shared.domain.repository

import com.merchpulse.shared.domain.model.Employee
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val currentEmployee: StateFlow<Employee?>
    suspend fun startSession(employee: Employee)
    suspend fun endSession()
}
