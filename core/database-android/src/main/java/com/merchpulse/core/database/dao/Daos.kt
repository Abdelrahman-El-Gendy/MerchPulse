package com.merchpulse.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.merchpulse.core.database.entity.AuditEntity
import com.merchpulse.core.database.entity.EmployeeEntity
import com.merchpulse.core.database.entity.EmployeePermissionEntity
import com.merchpulse.core.database.entity.EmployeeWithPermissions
import com.merchpulse.core.database.entity.ProductEntity
import com.merchpulse.core.database.entity.PunchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE ownerUserId = :userId AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getAllProducts(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id AND ownerUserId = :userId AND isDeleted = 0")
    fun getProductById(userId: String, id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE status = :status AND ownerUserId = :userId AND isDeleted = 0 ORDER BY name ASC")
    fun getProductsByStatus(userId: String, status: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stockQty <= lowStockThreshold AND ownerUserId = :userId AND isDeleted = 0 ORDER BY stockQty ASC")
    fun getLowStockProducts(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE stockQty <= lowStockThreshold AND ownerUserId = :userId AND isDeleted = 0")
    fun getLowStockCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE status = 'UPCOMING' AND ownerUserId = :userId AND isDeleted = 0")
    fun getUpcomingCount(userId: String): Flow<Int>

    @Query("""
        SELECT * FROM products 
        WHERE ownerUserId = :userId 
        AND (name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%') 
        AND isDeleted = 0 
        ORDER BY name ASC
    """)
    fun searchProducts(userId: String, query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isDeleted = 1 WHERE id = :id AND ownerUserId = :userId")
    suspend fun softDeleteProduct(userId: String, id: String)

    @Query("UPDATE products SET stockQty = :newQty, updatedAt = :updatedAt WHERE id = :id AND ownerUserId = :userId")
    suspend fun adjustStock(userId: String, id: String, newQty: Int, updatedAt: Long)

    @Query("DELETE FROM products WHERE ownerUserId = :userId")
    suspend fun clearCacheForUser(userId: String)
}

@Dao
interface EmployeeDao {
    @Transaction
    @Query("SELECT * FROM employees WHERE id = :id AND ownerUserId = :userId")
    fun getEmployeeById(userId: String, id: String): Flow<EmployeeWithPermissions?>

    @Transaction
    @Query("SELECT * FROM employees WHERE email = :email AND ownerUserId = :userId")
    fun getEmployeeByEmail(userId: String, email: String): Flow<EmployeeWithPermissions?>

    @Transaction
    @Query("SELECT * FROM employees WHERE phoneNumber = :phone AND ownerUserId = :userId")
    fun getEmployeeByPhone(userId: String, phone: String): Flow<EmployeeWithPermissions?>

    @Transaction
    @Query("SELECT * FROM employees WHERE ownerUserId = :userId ORDER BY fullName ASC")
    fun getAllEmployees(userId: String): Flow<List<EmployeeWithPermissions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<EmployeePermissionEntity>)

    @Query("DELETE FROM employee_permissions WHERE employeeId = :employeeId AND ownerUserId = :userId")
    suspend fun clearPermissions(userId: String, employeeId: String)

    @Transaction
    suspend fun updateEmployeeWithPermissions(userId: String, employee: EmployeeEntity, permissions: List<String>) {
        updateEmployee(employee)
        clearPermissions(userId, employee.id)
        insertPermissions(permissions.map { EmployeePermissionEntity(employee.id, it, userId) })
    }

    @Transaction
    suspend fun insertEmployeeWithPermissions(userId: String, employee: EmployeeEntity, permissions: List<String>) {
        insertEmployee(employee)
        insertPermissions(permissions.map { EmployeePermissionEntity(employee.id, it, userId) })
    }

    @Query("DELETE FROM employees WHERE ownerUserId = :userId")
    suspend fun clearCacheForUser(userId: String)

    @Query("DELETE FROM employee_permissions WHERE ownerUserId = :userId")
    suspend fun clearPermissionsForUser(userId: String)
}

@Dao
interface PunchDao {
    @Query("SELECT * FROM punches WHERE employeeId = :employeeId AND ownerUserId = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    fun getPunchesForEmployee(userId: String, employeeId: String, from: Long, to: Long): Flow<List<PunchEntity>>

    @Query("SELECT * FROM punches WHERE ownerUserId = :userId AND timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun getAllPunches(userId: String, from: Long, to: Long): Flow<List<PunchEntity>>

    @Query("SELECT * FROM punches WHERE employeeId = :employeeId AND ownerUserId = :userId ORDER BY timestamp DESC LIMIT 1")
    fun getLastPunch(userId: String, employeeId: String): Flow<PunchEntity?>

    @Query("SELECT COUNT(*) FROM punches WHERE ownerUserId = :userId AND timestamp BETWEEN :from AND :to")
    fun getTodayPunchCount(userId: String, from: Long, to: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPunch(punch: PunchEntity)

    @Update
    suspend fun updatePunch(punch: PunchEntity)

    @Query("DELETE FROM punches WHERE ownerUserId = :userId")
    suspend fun clearCacheForUser(userId: String)
}

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AuditEntity)

    @Query("SELECT * FROM audit_logs WHERE entityId = :entityId AND entityType = :entityType AND ownerUserId = :userId ORDER BY timestamp DESC")
    fun getAuditsForEntity(userId: String, entityId: String, entityType: String): Flow<List<AuditEntity>>

    @Query("SELECT * FROM audit_logs WHERE ownerUserId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAudits(userId: String, limit: Int = 50): Flow<List<AuditEntity>>

    @Query("DELETE FROM audit_logs WHERE ownerUserId = :userId")
    suspend fun clearCacheForUser(userId: String)
}
