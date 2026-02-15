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
    
    private val prefs = context.getSharedPreferences("merchpulse_session", Context.MODE_PRIVATE)
    private val keyEmployee = "current_employee"
    
    private val _currentEmployee = MutableStateFlow<Employee?>(loadSession())
    override val currentEmployee = _currentEmployee.asStateFlow()

    private fun loadSession(): Employee? {
        val json = prefs.getString(keyEmployee, null) ?: return null
        return try {
            Json.decodeFromString<Employee>(json)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun startSession(employee: Employee) {
        _currentEmployee.value = employee
        val json = Json.encodeToString(employee)
        prefs.edit().putString(keyEmployee, json).apply()
    }

    override suspend fun endSession() {
        _currentEmployee.value = null
        prefs.edit().remove(keyEmployee).apply()
    }
}
