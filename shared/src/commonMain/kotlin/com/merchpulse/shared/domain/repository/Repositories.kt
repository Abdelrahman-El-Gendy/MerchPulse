package com.merchpulse.shared.domain.repository

import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.domain.model.TimePunch
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Instant

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    fun getProductsByStatus(status: ProductStatus): Flow<List<Product>>
    fun getLowStockProducts(threshold: Int = 0): Flow<List<Product>>
    fun searchProducts(query: String): Flow<List<Product>>
    fun getLowStockCount(): Flow<Int>
    fun getUpcomingCount(): Flow<Int>
    
    suspend fun createProduct(product: Product): Result<Unit>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun softDeleteProduct(id: String): Result<Unit>
    suspend fun adjustStock(id: String, newQty: Int): Result<Unit>
}

interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    fun getEmployeeById(id: String): Flow<Employee?>
    fun getEmployeeByEmail(email: String): Flow<Employee?>
    fun getEmployeeByPhone(phone: String): Flow<Employee?>
    
    suspend fun createEmployee(employee: Employee, pinHash: String): Result<Unit>
    suspend fun updateEmployee(employee: Employee): Result<Unit>
    suspend fun verifyPin(employeeId: String, pin: String): Boolean
}

interface PunchRepository {
    fun getPunchesForEmployee(employeeId: String, from: Instant, to: Instant): Flow<List<TimePunch>>
    fun getLastPunch(employeeId: String): Flow<TimePunch?>
    fun getAllPunches(from: Instant, to: Instant): Flow<List<TimePunch>>
    fun getTodayPunchCount(from: Instant, to: Instant): Flow<Int>
    
    suspend fun recordPunch(punch: TimePunch): Result<Unit>
    suspend fun updatePunch(punch: TimePunch): Result<Unit> // for corrections
}

interface AuditRepository {
    suspend fun logAction(
        action: String,
        entityType: String,
        entityId: String,
        previousState: String?,
        newState: String?,
        note: String? = null
    ): Result<Unit>
    
    fun getRecentLogs(limit: Int = 50): Flow<List<com.merchpulse.shared.domain.model.AuditLog>>
}
