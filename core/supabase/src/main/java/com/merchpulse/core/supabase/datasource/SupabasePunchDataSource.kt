package com.merchpulse.core.supabase.datasource

import com.merchpulse.core.supabase.dto.*
import com.merchpulse.core.supabase.mapper.*
import com.merchpulse.shared.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Remote data source for Time Punches backed by Supabase Postgrest.
 */
interface SupabasePunchDataSource {
    suspend fun fetchPunchesForEmployee(employeeId: String): List<TimePunch>
    suspend fun fetchPunchesBetween(startEpoch: Long, endEpoch: Long): List<TimePunch>
    suspend fun insertPunch(punch: TimePunch): Result<Unit>
    suspend fun updatePunch(punch: TimePunch): Result<Unit>
    suspend fun fetchLastPunch(employeeId: String): TimePunch?
}

class SupabasePunchDataSourceImpl(
    private val client: SupabaseClient
) : SupabasePunchDataSource {

    companion object {
        private const val TABLE = "punches"
    }

    override suspend fun fetchPunchesForEmployee(employeeId: String): List<TimePunch> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select {
                filter { eq("employee_id", employeeId) }
            }
            .decodeList<PunchDto>()
            .map { it.toDomain() }
    }

    override suspend fun fetchPunchesBetween(startEpoch: Long, endEpoch: Long): List<TimePunch> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select {
                filter {
                    gte("timestamp", startEpoch)
                    lte("timestamp", endEpoch)
                }
            }
            .decodeList<PunchDto>()
            .map { it.toDomain() }
    }

    override suspend fun insertPunch(punch: TimePunch): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE].insert(punch.toDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePunch(punch: TimePunch): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE].update({
                set("timestamp", punch.timestamp.toEpochMilliseconds())
                set("type", punch.type.name)
                set("note", punch.note)
            }) {
                filter { eq("id", punch.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun fetchLastPunch(employeeId: String): TimePunch? = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE]
                .select {
                    filter { eq("employee_id", employeeId) }
                    order("timestamp", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<PunchDto>()
                ?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
