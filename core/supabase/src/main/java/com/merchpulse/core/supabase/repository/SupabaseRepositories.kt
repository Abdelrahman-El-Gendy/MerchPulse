package com.merchpulse.core.supabase.repository

import com.merchpulse.core.supabase.datasource.SupabaseAuditDataSource
import com.merchpulse.core.supabase.datasource.SupabaseEmployeeDataSource
import com.merchpulse.core.supabase.datasource.SupabaseProductDataSource
import com.merchpulse.core.supabase.datasource.SupabasePunchDataSource
import com.merchpulse.shared.domain.model.AuditLog
import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.domain.model.TimePunch
import com.merchpulse.shared.domain.repository.AuditRepository
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.domain.repository.PunchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

class SupabaseProductRepository(private val dataSource: SupabaseProductDataSource) : ProductRepository {
    override fun getAllProducts(): Flow<List<Product>> = flow {
        emit(dataSource.fetchAllProducts())
    }

    override fun getProductById(id: String): Flow<Product?> = flow {
        emit(dataSource.fetchProductById(id))
    }

    override fun getProductsByStatus(status: ProductStatus): Flow<List<Product>> = flow {
        // Not optimized, fetches all and filters. 
        // Ideal: dataSource.fetchProductsByStatus(status)
        emit(dataSource.fetchAllProducts().filter { it.status == status })
    }

    override fun getLowStockProducts(threshold: Int): Flow<List<Product>> = flow {
        emit(dataSource.fetchLowStockProducts(threshold))
    }

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        // Not optimized, fetches all and filters.
        // Ideal: dataSource.searchProducts(query)
        val all = dataSource.fetchAllProducts()
        emit(all.filter { it.name.contains(query, ignoreCase = true) || it.sku.contains(query, ignoreCase = true) })
    }

    override fun getLowStockCount(): Flow<Int> = flow {
         // Not optimized
         emit(dataSource.fetchLowStockProducts().size)
    }

    override fun getUpcomingCount(): Flow<Int> = flow {
         // Upcoming logic not in dataSource, assuming specific status
         emit(0) 
    }

    override suspend fun createProduct(product: Product): Result<Unit> {
        return dataSource.upsertProduct(product)
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return dataSource.upsertProduct(product)
    }

    override suspend fun softDeleteProduct(id: String): Result<Unit> {
        return dataSource.deleteProduct(id)
    }

    override suspend fun adjustStock(id: String, newQty: Int): Result<Unit> {
        return dataSource.updateStockQty(id, newQty)
    }
}

class SupabaseEmployeeRepository(private val dataSource: SupabaseEmployeeDataSource) : EmployeeRepository {
    override fun getAllEmployees(): Flow<List<Employee>> = flow {
        emit(dataSource.fetchAllEmployees())
    }

    override fun getEmployeeById(id: String): Flow<Employee?> = flow {
        emit(dataSource.fetchEmployeeById(id))
    }

    override fun getEmployeeByEmail(email: String): Flow<Employee?> = flow {
        emit(dataSource.fetchEmployeeByEmail(email))
    }

    override fun getEmployeeByPhone(phone: String): Flow<Employee?> = flow {
        emit(dataSource.fetchEmployeeByPhone(phone))
    }

    override suspend fun createEmployee(employee: Employee, pinHash: String): Result<Unit> {
        return dataSource.upsertEmployee(employee, pinHash)
    }

    override suspend fun updateEmployee(employee: Employee): Result<Unit> {
        // Assuming pinHash is not changed here or handled by fetching existing.
        // But upsert needs pinHash. 
        // We fetch existing hash to preserve it.
        val existingHash = dataSource.fetchPinHash(employee.id) ?: return Result.failure(Exception("Employee not found"))
        return dataSource.upsertEmployee(employee, existingHash)
    }

    override suspend fun verifyPin(employeeId: String, pin: String): Boolean {
        val hash = dataSource.fetchPinHash(employeeId) ?: return false
        // Simple plaintext check for MVP, as per previous implementation
        return hash == pin
    }
}

class SupabasePunchRepository(private val dataSource: SupabasePunchDataSource) : PunchRepository {
    override fun getPunchesForEmployee(employeeId: String, from: Instant, to: Instant): Flow<List<TimePunch>> = flow {
        val punches = dataSource.fetchPunchesBetween(from.toEpochMilliseconds(), to.toEpochMilliseconds())
        emit(punches.filter { it.employeeId == employeeId })
    }

    override fun getLastPunch(employeeId: String): Flow<TimePunch?> = flow {
        emit(dataSource.fetchLastPunch(employeeId))
    }

    override fun getAllPunches(from: Instant, to: Instant): Flow<List<TimePunch>> = flow {
        emit(dataSource.fetchPunchesBetween(from.toEpochMilliseconds(), to.toEpochMilliseconds()))
    }

    override fun getTodayPunchCount(from: Instant, to: Instant): Flow<Int> = flow {
        emit(dataSource.fetchPunchesBetween(from.toEpochMilliseconds(), to.toEpochMilliseconds()).size)
    }

    override suspend fun recordPunch(punch: TimePunch): Result<Unit> {
        return dataSource.insertPunch(punch)
    }

    override suspend fun updatePunch(punch: TimePunch): Result<Unit> {
        return dataSource.updatePunch(punch)
    }
}

class SupabaseAuditRepository(private val dataSource: SupabaseAuditDataSource) : AuditRepository {
    override suspend fun logAction(
        action: String,
        entityType: String,
        entityId: String,
        previousState: String?,
        newState: String?,
        note: String?
    ): Result<Unit> {
        val log = AuditLog(
            id = UUID.randomUUID().toString(),
            action = action,
            entityType = entityType,
            entityId = entityId,
            previousState = previousState,
            newState = newState,
            timestamp = Clock.System.now(),
            performedBy = "Unknown", // Ideally injected or passed
            note = note
        )
        return dataSource.insertAuditLog(log)
    }

    override fun getRecentLogs(limit: Int): Flow<List<AuditLog>> = flow {
        emit(dataSource.fetchRecentAuditLogs(limit))
    }
}
