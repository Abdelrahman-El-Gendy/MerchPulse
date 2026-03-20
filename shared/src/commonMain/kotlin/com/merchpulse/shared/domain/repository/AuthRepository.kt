package com.merchpulse.shared.domain.repository

import com.merchpulse.shared.domain.model.Role
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain-specific session representation.
 */
data class AuthSession(
    val userId: String,
    val email: String,
    val fullName: String,
    val role: Role,
    val accessToken: String,
    val expiresAt: Long
)

/**
 * Handle authentication with Supabase.
 */
interface AuthRepository {
    val currentSession: StateFlow<AuthSession?>
    
    suspend fun signIn(email: String, pin: String): Result<AuthSession>
    suspend fun signUp(email: String, pin: String, fullName: String): Result<AuthSession>
    suspend fun signOut(): Result<Unit>
    
    /**
     * Re-verifies existing Supabase session.
     */
    suspend fun restoreSession(): Result<AuthSession?>
}
