package com.merchpulse.shared.domain.repository

import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Role
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val currentEmployee: StateFlow<Employee?>
    val currentUserId: String?
    val currentUserRole: Role?
    
    suspend fun startSession(employee: Employee)
    suspend fun endSession()
}
