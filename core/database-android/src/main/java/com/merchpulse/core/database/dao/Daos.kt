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
    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY updatedAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id AND isDeleted = 0")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE status = :status AND isDeleted = 0 ORDER BY name ASC")
    fun getProductsByStatus(status: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stockQty <= lowStockThreshold AND isDeleted = 0 ORDER BY stockQty ASC")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products WHERE stockQty <= lowStockThreshold AND isDeleted = 0")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE status = 'UPCOMING' AND isDeleted = 0")
    fun getUpcomingCount(): Flow<Int>

    @Query("""
        SELECT * FROM products 
        WHERE (name LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%') 
        AND isDeleted = 0 
        ORDER BY name ASC
    """)
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("UPDATE products SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteProduct(id: String)

    @Query("UPDATE products SET stockQty = :newQty, updatedAt = :updatedAt WHERE id = :id")
    suspend fun adjustStock(id: String, newQty: Int, updatedAt: Long)
}

@Dao
interface EmployeeDao {
    @Transaction
    @Query("SELECT * FROM employees WHERE id = :id")
    fun getEmployeeById(id: String): Flow<EmployeeWithPermissions?>

    @Transaction
    @Query("SELECT * FROM employees WHERE email = :email")
    fun getEmployeeByEmail(email: String): Flow<EmployeeWithPermissions?>

    @Transaction
    @Query("SELECT * FROM employees ORDER BY fullName ASC")
    fun getAllEmployees(): Flow<List<EmployeeWithPermissions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermissions(permissions: List<EmployeePermissionEntity>)

    @Query("DELETE FROM employee_permissions WHERE employeeId = :employeeId")
    suspend fun clearPermissions(employeeId: String)

    @Transaction
    suspend fun updateEmployeeWithPermissions(employee: EmployeeEntity, permissions: List<String>) {
        updateEmployee(employee)
        clearPermissions(employee.id)
        insertPermissions(permissions.map { EmployeePermissionEntity(employee.id, it) })
    }

    @Transaction
    suspend fun insertEmployeeWithPermissions(employee: EmployeeEntity, permissions: List<String>) {
        insertEmployee(employee)
        insertPermissions(permissions.map { EmployeePermissionEntity(employee.id, it) })
    }
}

@Dao
interface PunchDao {
    @Query("SELECT * FROM punches WHERE employeeId = :employeeId AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    fun getPunchesForEmployee(employeeId: String, from: Long, to: Long): Flow<List<PunchEntity>>

    @Query("SELECT * FROM punches WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun getAllPunches(from: Long, to: Long): Flow<List<PunchEntity>>

    @Query("SELECT * FROM punches WHERE employeeId = :employeeId ORDER BY timestamp DESC LIMIT 1")
    fun getLastPunch(employeeId: String): Flow<PunchEntity?>

    @Query("SELECT COUNT(*) FROM punches WHERE timestamp BETWEEN :from AND :to")
    fun getTodayPunchCount(from: Long, to: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPunch(punch: PunchEntity)

    @Update
    suspend fun updatePunch(punch: PunchEntity)
}

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AuditEntity)

    @Query("SELECT * FROM audit_logs WHERE entityId = :entityId AND entityType = :entityType ORDER BY timestamp DESC")
    fun getAuditsForEntity(entityId: String, entityType: String): Flow<List<AuditEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAudits(limit: Int = 50): Flow<List<AuditEntity>>
}
