package com.merchpulse.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["ownerUserId"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val ownerUserId: String,  // Scope to logged-in user
    val sku: String,
    val name: String,
    val description: String?,
    val category: String?,
    val status: String,
    val price: Double,
    val cost: Double?,
    val currency: String,
    val stockQty: Int,
    val lowStockThreshold: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean
)

@Entity(
    tableName = "employees",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phoneNumber"], unique = true),
        Index(value = ["ownerUserId"])
    ]
)
data class EmployeeEntity(
    @PrimaryKey
    val id: String,
    val ownerUserId: String,  // Scope to logged-in user
    val email: String,
    val phoneNumber: String,
    val fullName: String,
    val role: String,
    val isActive: Boolean,
    val isOnShift: Boolean,
    val lastPunchTime: Long?,
    val imageUrl: String?,
    val joinedAt: Long,
    val pinHash: String
)


@Entity(
    tableName = "employee_permissions",
    primaryKeys = ["employeeId", "permission", "ownerUserId"],
    indices = [Index(value = ["ownerUserId"])]
)
data class EmployeePermissionEntity(
    val employeeId: String,
    val permission: String,
    val ownerUserId: String
)

@Entity(
    tableName = "punches",
    indices = [
        Index(value = ["employeeId", "timestamp"]),
        Index(value = ["ownerUserId"])
    ]
)
data class PunchEntity(
    @PrimaryKey
    val id: String,
    val ownerUserId: String,
    val employeeId: String,
    val timestamp: Long,
    val type: String,
    val deviceId: String?,
    val note: String?,
    val createdBy: String
)

@Entity(
    tableName = "audit_logs",
    indices = [
        Index(value = ["entityId", "entityType"]),
        Index(value = ["ownerUserId"])
    ]
)
data class AuditEntity(
    @PrimaryKey
    val id: String,
    val ownerUserId: String,
    val entityType: String,   // "PUNCH", "PRODUCT", "EMPLOYEE"
    val entityId: String,
    val action: String,       // "CREATE", "UPDATE", "DELETE", "CORRECTION"
    val performedBy: String,  // employeeId
    val oldValue: String?,
    val newValue: String?,
    val timestamp: Long,
    val reason: String?
)
