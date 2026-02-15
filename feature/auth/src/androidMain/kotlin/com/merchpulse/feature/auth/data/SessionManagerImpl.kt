package com.merchpulse.feature.auth.data

import android.content.Context
import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionManagerImpl(
    private val context: Context
) : SessionManager {
    
    // In production, use EncryptedSharedPreferences to persist and read on init
    private val _currentEmployee = MutableStateFlow<Employee?>(null)
    override val currentEmployee = _currentEmployee.asStateFlow()

    override suspend fun startSession(employee: Employee) {
        _currentEmployee.value = employee
        // Save to EncryptedSharedPreferences
    }

    override suspend fun endSession() {
        _currentEmployee.value = null
        // Clear EncryptedSharedPreferences
    }
}
