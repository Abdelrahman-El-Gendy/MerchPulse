package com.merchpulse.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class ProductStatus {
    UPCOMING, ACTIVE, DISCONTINUED
}

@Serializable
data class Product(
    val id: String,
    val sku: String,
    val name: String,
    val description: String?,
    val category: String?,
    val status: ProductStatus,
    val price: Double,
    val cost: Double?,
    val currency: String,
    val stockQty: Int,
    val lowStockThreshold: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isDeleted: Boolean
)

@Serializable
enum class PunchType {
    IN, OUT
}

@Serializable
data class TimePunch(
    val id: String,
    val employeeId: String,
    val timestamp: Instant,
    val type: PunchType,
    val deviceId: String?,
    val note: String?,
    val createdBy: String // employeeId or managerId for corrections
)

@Serializable
enum class Role {
    ADMIN, MANAGER, STAFF
}

@Serializable
enum class Permission {
    PRODUCT_VIEW,
    PRODUCT_CREATE,
    PRODUCT_EDIT,
    PRODUCT_DELETE,
    STOCK_ADJUST,
    EMPLOYEE_VIEW,
    EMPLOYEE_MANAGE,
    PUNCH_SELF,
    PUNCH_VIEW_ALL,
    PUNCH_ADJUST
}

@Serializable
data class Employee(
    val id: String,
    val email: String,
    val fullName: String,
    val role: Role,
    val permissions: Set<Permission>,
    val isActive: Boolean,
    val joinedAt: Instant
)

@Serializable
data class AuditLog(
    val id: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val performedBy: String,
    val timestamp: Instant,
    val previousState: String?,
    val newState: String?,
    val note: String?
)
