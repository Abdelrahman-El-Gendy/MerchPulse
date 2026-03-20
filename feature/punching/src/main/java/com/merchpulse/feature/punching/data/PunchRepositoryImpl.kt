package com.merchpulse.feature.punching.data

import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.core.database.dao.PunchDao
import com.merchpulse.core.database.entity.PunchEntity
import com.merchpulse.shared.domain.model.PunchType
import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.domain.repository.PunchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.emptyFlow

class PunchRepositoryImpl(
    private val punchDao: PunchDao,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : PunchRepository {

    override fun getPunchesForEmployee(employeeId: String, from: Instant, to: Instant): Flow<List<TimePunch>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return punchDao.getPunchesForEmployee(userId, employeeId, from.toEpochMilliseconds(), to.toEpochMilliseconds())
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getLastPunch(employeeId: String): Flow<TimePunch?> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return punchDao.getLastPunch(userId, employeeId).map { it?.toDomain() }
    }

    override fun getAllPunches(from: Instant, to: Instant): Flow<List<TimePunch>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return punchDao.getAllPunches(userId, from.toEpochMilliseconds(), to.toEpochMilliseconds())
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun recordPunch(punch: TimePunch): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            punchDao.insertPunch(punch.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePunch(punch: TimePunch): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            punchDao.updatePunch(punch.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTodayPunchCount(from: Instant, to: Instant): Flow<Int> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return punchDao.getTodayPunchCount(userId, from.toEpochMilliseconds(), to.toEpochMilliseconds())
    }
}

fun PunchEntity.toDomain(): TimePunch {
    return TimePunch(
        id = id,
        employeeId = employeeId,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        type = PunchType.valueOf(type),
        deviceId = deviceId,
        note = note,
        createdBy = createdBy
    )
}

fun TimePunch.toEntity(ownerUserId: String): PunchEntity {
    return PunchEntity(
        id = id,
        ownerUserId = ownerUserId,
        employeeId = employeeId,
        timestamp = timestamp.toEpochMilliseconds(),
        type = type.name,
        deviceId = deviceId,
        note = note,
        createdBy = createdBy
    )
}
