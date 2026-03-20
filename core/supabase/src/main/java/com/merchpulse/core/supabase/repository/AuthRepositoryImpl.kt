package com.merchpulse.core.supabase.repository

import com.merchpulse.core.database.AppDatabase
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.domain.repository.AuthRepository
import com.merchpulse.shared.domain.repository.AuthSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.jsonPrimitive

class AuthRepositoryImpl(
    private val supabase: SupabaseClient,
    private val roomDb: AppDatabase
) : AuthRepository {

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    override val currentSession = _currentSession.asStateFlow()

    override suspend fun signIn(email: String, pin: String): Result<AuthSession> = runCatching {
        // STEP 1: Always fully sign out previous user first
        signOut()

        // STEP 2: Sign in new user
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = pin
        }

        // STEP 3: Read the NEW session
        val freshSession = supabase.auth.currentSessionOrNull()
            ?: throw Exception("Session not created")

        // STEP 4: Extract role from JWT user_metadata
        val roleStr = freshSession.user?.userMetadata?.get("role")?.jsonPrimitive?.content
        val role = roleStr?.let { 
            try { Role.valueOf(it) } catch (e: Exception) { Role.STAFF }
        } ?: Role.STAFF

        val fullName = freshSession.user?.userMetadata?.get("fullName")?.jsonPrimitive?.content ?: ""

        val authSession = AuthSession(
            userId = freshSession.user!!.id,
            email = email,
            fullName = fullName,
            role = role,
            accessToken = freshSession.accessToken,
            expiresAt = freshSession.expiresAt.toEpochMilliseconds()
        )

        _currentSession.value = authSession
        authSession
    }

    override suspend fun signUp(email: String, pin: String, fullName: String): Result<AuthSession> = runCatching {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = pin
            data = kotlinx.serialization.json.JsonObject(
                mapOf(
                    "role" to kotlinx.serialization.json.JsonPrimitive(Role.STAFF.name),
                    "fullName" to kotlinx.serialization.json.JsonPrimitive(fullName)
                )
            )
        }
        
        signIn(email, pin).getOrThrow()
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        val userId = _currentSession.value?.userId 
            ?: supabase.auth.currentSessionOrNull()?.user?.id

        // Clear local state
        _currentSession.value = null
        
        // Clear Supabase session
        supabase.auth.signOut()
        
        // Wipe local Scoped Cache
        if (userId != null) {
            roomDb.clearAllTablesForCurrentUser(userId)
        }
    }

    override suspend fun restoreSession(): Result<AuthSession?> = runCatching {
        val session = supabase.auth.currentSessionOrNull() ?: return@runCatching null
        
        val roleStr = session.user?.userMetadata?.get("role")?.jsonPrimitive?.content
        val role = roleStr?.let { 
            try { Role.valueOf(it) } catch (e: Exception) { Role.STAFF }
        } ?: Role.STAFF

        val fullName = session.user?.userMetadata?.get("fullName")?.jsonPrimitive?.content ?: ""

        val authSession = AuthSession(
            userId = session.user!!.id,
            email = session.user!!.email ?: "",
            fullName = fullName,
            role = role,
            accessToken = session.accessToken,
            expiresAt = session.expiresAt.toEpochMilliseconds()
        )

        _currentSession.value = authSession
        authSession
    }
}
