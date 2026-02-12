package com.merchpulse.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index(value = ["sku"], unique = true)]
)
data class ProductEntity(
    @PrimaryKey
    val id: String,
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
    indices = [Index(value = ["email"], unique = true)]
)
data class EmployeeEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val isActive: Boolean,
    val joinedAt: Long,
    val pinHash: String
)

@Entity(
    tableName = "employee_permissions",
    primaryKeys = ["employeeId", "permission"]
)
data class EmployeePermissionEntity(
    val employeeId: String,
    val permission: String
)

@Entity(
    tableName = "punches",
    indices = [Index(value = ["employeeId", "timestamp"])]
)
data class PunchEntity(
    @PrimaryKey
    val id: String,
    val employeeId: String,
    val timestamp: Long,
    val type: String,
    val deviceId: String?,
    val note: String?,
    val createdBy: String
)

@Entity(
    tableName = "audit_logs",
    indices = [Index(value = ["entityId", "entityType"])]
)
data class AuditEntity(
    @PrimaryKey
    val id: String,
    val entityType: String,   // "PUNCH", "PRODUCT", "EMPLOYEE"
    val entityId: String,
    val action: String,       // "CREATE", "UPDATE", "DELETE", "CORRECTION"
    val performedBy: String,  // employeeId
    val oldValue: String?,
    val newValue: String?,
    val timestamp: Long,
    val reason: String?
)
