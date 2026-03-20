package com.merchpulse.core.supabase.datasource

import com.merchpulse.core.supabase.dto.*
import com.merchpulse.core.supabase.mapper.*
import com.merchpulse.shared.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Remote data source for Employees backed by Supabase Postgrest.
 */
interface SupabaseEmployeeDataSource {
    suspend fun fetchAllEmployees(): List<Employee>
    suspend fun fetchEmployeeById(id: String): Employee?
    suspend fun fetchEmployeeByEmail(email: String): Employee?
    suspend fun fetchEmployeeByPhone(phone: String): Employee?
    suspend fun upsertEmployee(employee: Employee, pinHash: String): Result<Unit>
    suspend fun fetchPermissions(employeeId: String): List<Permission>
    suspend fun fetchPinHash(employeeId: String): String?
}

class SupabaseEmployeeDataSourceImpl(
    private val client: SupabaseClient
) : SupabaseEmployeeDataSource {

    companion object {
        private const val EMPLOYEES_TABLE = "employees"
        private const val PERMISSIONS_TABLE = "employee_permissions"
    }

    override suspend fun fetchAllEmployees(): List<Employee> = withContext(Dispatchers.IO) {
        val employees = client.postgrest[EMPLOYEES_TABLE]
            .select()
            .decodeList<EmployeeDto>()

        employees.map { emp ->
            val permissions = client.postgrest[PERMISSIONS_TABLE]
                .select { filter { eq("employee_id", emp.id) } }
                .decodeList<EmployeePermissionDto>()
            emp.toDomain(permissions)
        }
    }

    override suspend fun fetchEmployeeById(id: String): Employee? = withContext(Dispatchers.IO) {
        try {
            val emp = client.postgrest[EMPLOYEES_TABLE]
                .select { filter { eq("id", id) } }
                .decodeSingleOrNull<EmployeeDto>() ?: return@withContext null

            val permissions = client.postgrest[PERMISSIONS_TABLE]
                .select { filter { eq("employee_id", id) } }
                .decodeList<EmployeePermissionDto>()

            emp.toDomain(permissions)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchEmployeeByEmail(email: String): Employee? = withContext(Dispatchers.IO) {
        try {
            val emp = client.postgrest[EMPLOYEES_TABLE]
                .select { filter { eq("email", email) } }
                .decodeSingleOrNull<EmployeeDto>() ?: return@withContext null

            val permissions = client.postgrest[PERMISSIONS_TABLE]
                .select { filter { eq("employee_id", emp.id) } }
                .decodeList<EmployeePermissionDto>()

            emp.toDomain(permissions)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchEmployeeByPhone(phone: String): Employee? = withContext(Dispatchers.IO) {
        try {
            val emp = client.postgrest[EMPLOYEES_TABLE]
                .select { filter { eq("phone_number", phone) } }
                .decodeSingleOrNull<EmployeeDto>() ?: return@withContext null

            val permissions = client.postgrest[PERMISSIONS_TABLE]
                .select { filter { eq("employee_id", emp.id) } }
                .decodeList<EmployeePermissionDto>()

            emp.toDomain(permissions)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun upsertEmployee(employee: Employee, pinHash: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[EMPLOYEES_TABLE].upsert(employee.toInsertDto(pinHash))

            // Delete old permissions and re-insert
            client.postgrest[PERMISSIONS_TABLE].delete {
                filter { eq("employee_id", employee.id) }
            }
            val permDtos = employee.toPermissionDtos()
            if (permDtos.isNotEmpty()) {
                client.postgrest[PERMISSIONS_TABLE].insert(permDtos)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchPermissions(employeeId: String): List<Permission> = withContext(Dispatchers.IO) {
        client.postgrest[PERMISSIONS_TABLE]
            .select { filter { eq("employee_id", employeeId) } }
            .decodeList<EmployeePermissionDto>()
            .map { Permission.valueOf(it.permission) }
    }

    override suspend fun fetchPinHash(employeeId: String): String? = withContext(Dispatchers.IO) {
        try {
            client.postgrest[EMPLOYEES_TABLE]
                .select { filter { eq("id", employeeId) } }
                .decodeSingleOrNull<EmployeeDto>()
                ?.pinHash
        } catch (e: Exception) {
            null
        }
    }
}
