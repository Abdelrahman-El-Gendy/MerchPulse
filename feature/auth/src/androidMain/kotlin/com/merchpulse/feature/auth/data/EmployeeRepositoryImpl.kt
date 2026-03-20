package com.merchpulse.feature.auth.data

import com.merchpulse.core.database.dao.EmployeeDao
import com.merchpulse.core.database.entity.EmployeeEntity
import com.merchpulse.core.database.entity.EmployeeWithPermissions
import com.merchpulse.shared.domain.model.Employee
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Role
import com.merchpulse.shared.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.merchpulse.core.common.DispatcherProvider

import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.emptyFlow

class EmployeeRepositoryImpl(
    private val employeeDao: EmployeeDao,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : EmployeeRepository {

    override fun getEmployeeByEmail(email: String): Flow<Employee?> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return employeeDao.getEmployeeByEmail(userId, email).map { it?.toDomain() }
    }

    override fun getEmployeeByPhone(phone: String): Flow<Employee?> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return employeeDao.getEmployeeByPhone(userId, phone).map { it?.toDomain() }
    }
    
    override fun getEmployeeById(id: String): Flow<Employee?> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return employeeDao.getEmployeeById(userId, id).map { it?.toDomain() }
    }

    override fun getAllEmployees(): Flow<List<Employee>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return employeeDao.getAllEmployees(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createEmployee(employee: Employee, pinHash: String): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            val entity = employee.toEntity(userId, pinHash)
            employeeDao.insertEmployee(entity)
            employeeDao.insertPermissions(employee.permissions.map { 
                com.merchpulse.core.database.entity.EmployeePermissionEntity(employee.id, it.name, userId) 
            })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmployee(employee: Employee): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            val existing = employeeDao.getEmployeeById(userId, employee.id).first()
            if (existing != null) {
                // Keep existing pinHash from DB
                val newEntity = employee.toEntity(userId, existing.employee.pinHash)
                employeeDao.updateEmployeeWithPermissions(userId, newEntity, employee.permissions.map { it.name })
                Result.success(Unit)
            } else {
                Result.failure(Exception("Employee not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyPin(employeeId: String, pin: String): Boolean = withContext(dispatcherProvider.io) {
         val userId = sessionManager.currentUserId ?: return@withContext false
         val employeeWithPerms = employeeDao.getEmployeeById(userId, employeeId).first()
         // MVP: direct comparison, in production use BCrypt/Argon2
         employeeWithPerms?.employee?.pinHash == pin
    }
}

private fun EmployeeWithPermissions.toDomain(): Employee {
    return Employee(
        id = employee.id,
        email = employee.email,
        phoneNumber = employee.phoneNumber,
        fullName = employee.fullName,
        role = Role.valueOf(employee.role),
        permissions = permissionEntities.map { Permission.valueOf(it.permission) }.toSet(),
        isActive = employee.isActive,
        joinedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(employee.joinedAt)
    )
}

private fun Employee.toEntity(ownerUserId: String, pinHash: String): EmployeeEntity {
    return EmployeeEntity(
        id = id,
        ownerUserId = ownerUserId,
        email = email,
        phoneNumber = phoneNumber,
        fullName = fullName,
        role = role.name,
        isActive = isActive,
        isOnShift = false,
        lastPunchTime = null,
        imageUrl = null,
        joinedAt = joinedAt.toEpochMilliseconds(),
        pinHash = pinHash
    )
}
