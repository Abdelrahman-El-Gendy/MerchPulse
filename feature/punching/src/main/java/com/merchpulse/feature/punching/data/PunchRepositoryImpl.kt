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

class PunchRepositoryImpl(
    private val punchDao: PunchDao,
    private val dispatcherProvider: DispatcherProvider
) : PunchRepository {

    override fun getPunchesForEmployee(employeeId: String, from: Instant, to: Instant): Flow<List<TimePunch>> {
        return punchDao.getPunchesForEmployee(employeeId, from.toEpochMilliseconds(), to.toEpochMilliseconds())
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getLastPunch(employeeId: String): Flow<TimePunch?> {
        return punchDao.getLastPunch(employeeId).map { it?.toDomain() }
    }

    override fun getAllPunches(from: Instant, to: Instant): Flow<List<TimePunch>> {
        return punchDao.getAllPunches(from.toEpochMilliseconds(), to.toEpochMilliseconds())
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun recordPunch(punch: TimePunch): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            punchDao.insertPunch(punch.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePunch(punch: TimePunch): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            punchDao.updatePunch(punch.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTodayPunchCount(from: Instant, to: Instant): Flow<Int> {
        return punchDao.getTodayPunchCount(from.toEpochMilliseconds(), to.toEpochMilliseconds())
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

fun TimePunch.toEntity(): PunchEntity {
    return PunchEntity(
        id = id,
        employeeId = employeeId,
        timestamp = timestamp.toEpochMilliseconds(),
        type = type.name,
        deviceId = deviceId,
        note = note,
        createdBy = createdBy
    )
}
