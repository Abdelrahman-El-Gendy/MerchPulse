package com.merchpulse.shared.domain.policy

import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.first

class AuthorizationPolicy(
    private val sessionManager: SessionManager
) {
    suspend fun checkPermission(permission: Permission): Boolean {
        val employee = sessionManager.currentEmployee.first()
        return employee?.permissions?.contains(permission) ?: false
    }

    suspend fun requirePermission(permission: Permission) {
        if (!checkPermission(permission)) {
            throw UnauthorizedException("Missing permission: ${permission.name}")
        }
    }
}

class UnauthorizedException(message: String) : Exception(message)
