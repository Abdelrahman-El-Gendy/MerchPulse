package com.merchpulse.core.supabase.datasource

import com.merchpulse.core.supabase.dto.*
import com.merchpulse.core.supabase.mapper.*
import com.merchpulse.shared.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Remote data source for Audit Logs backed by Supabase Postgrest.
 */
interface SupabaseAuditDataSource {
    suspend fun fetchAuditLogs(entityId: String, entityType: String): List<AuditLog>
    suspend fun fetchRecentAuditLogs(limit: Int = 50): List<AuditLog>
    suspend fun insertAuditLog(log: AuditLog): Result<Unit>
}

class SupabaseAuditDataSourceImpl(
    private val client: SupabaseClient
) : SupabaseAuditDataSource {

    companion object {
        private const val TABLE = "audit_logs"
    }

    override suspend fun fetchAuditLogs(entityId: String, entityType: String): List<AuditLog> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select {
                filter {
                    eq("entity_id", entityId)
                    eq("entity_type", entityType)
                }
            }
            .decodeList<AuditLogDto>()
            .map { it.toDomain() }
    }

    override suspend fun fetchRecentAuditLogs(limit: Int): List<AuditLog> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select {
                order("timestamp", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<AuditLogDto>()
            .map { it.toDomain() }
    }

    override suspend fun insertAuditLog(log: AuditLog): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE].insert(log.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
