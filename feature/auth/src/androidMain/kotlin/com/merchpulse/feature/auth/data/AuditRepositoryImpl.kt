package com.merchpulse.feature.auth.data

import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.core.database.dao.AuditDao
import com.merchpulse.core.database.entity.AuditEntity
import com.merchpulse.shared.domain.model.AuditLog
import com.merchpulse.shared.domain.repository.AuditRepository
import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.util.UUID

class AuditRepositoryImpl(
    private val auditDao: AuditDao,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : AuditRepository {

    override suspend fun logAction(
        action: String,
        entityType: String,
        entityId: String,
        previousState: String?,
        newState: String?,
        note: String?
    ): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            val employee = sessionManager.currentEmployee.value
            val audit = AuditEntity(
                id = UUID.randomUUID().toString(),
                action = action,
                entityType = entityType,
                entityId = entityId,
                performedBy = employee?.id ?: "SYSTEM",
                timestamp = System.currentTimeMillis(),
                oldValue = previousState,
                newValue = newState,
                reason = note
            )
            auditDao.insertAudit(audit)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRecentLogs(limit: Int): Flow<List<AuditLog>> {
        return auditDao.getRecentAudits(limit).map { list ->
            list.map { it.toDomain() }
        }
    }
}

fun AuditEntity.toDomain(): AuditLog {
    return AuditLog(
        id = id,
        action = action,
        entityType = entityType,
        entityId = entityId,
        performedBy = performedBy,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        previousState = oldValue,
        newState = newValue,
        note = reason
    )
}
