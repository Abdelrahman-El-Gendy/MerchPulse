package com.merchpulse.core.supabase.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase Data Transfer Objects.
 *
 * These DTOs use @SerialName to match the exact column names in the
 * Supabase/Postgres tables (snake_case). They act as the bridge between
 * the network layer and the domain models.
 */

// ──────────────────────────────────────────────
// Employee DTOs
// ──────────────────────────────────────────────

@Serializable
data class EmployeeDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("role") val role: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_on_shift") val isOnShift: Boolean = false,
    @SerialName("last_punch_time") val lastPunchTime: Long? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("joined_at") val joinedAt: Long,
    @SerialName("pin_hash") val pinHash: String
)

@Serializable
data class EmployeePermissionDto(
    @SerialName("employee_id") val employeeId: String,
    @SerialName("permission") val permission: String
)

/**
 * Lightweight insert DTO (no server-generated fields).
 */
@Serializable
data class EmployeeInsertDto(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("role") val role: String,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_on_shift") val isOnShift: Boolean = false,
    @SerialName("joined_at") val joinedAt: Long,
    @SerialName("pin_hash") val pinHash: String
)

// ──────────────────────────────────────────────
// Product DTOs
// ──────────────────────────────────────────────

@Serializable
data class ProductDto(
    @SerialName("id") val id: String,
    @SerialName("sku") val sku: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("status") val status: String,
    @SerialName("price") val price: Double,
    @SerialName("cost") val cost: Double? = null,
    @SerialName("currency") val currency: String = "USD",
    @SerialName("stock_qty") val stockQty: Int,
    @SerialName("low_stock_threshold") val lowStockThreshold: Int,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("is_deleted") val isDeleted: Boolean = false
)

@Serializable
data class ProductInsertDto(
    @SerialName("id") val id: String,
    @SerialName("sku") val sku: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("status") val status: String = "ACTIVE",
    @SerialName("price") val price: Double,
    @SerialName("cost") val cost: Double? = null,
    @SerialName("currency") val currency: String = "USD",
    @SerialName("stock_qty") val stockQty: Int,
    @SerialName("low_stock_threshold") val lowStockThreshold: Int = 5,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("is_deleted") val isDeleted: Boolean = false
)

// ──────────────────────────────────────────────
// Punch DTOs
// ──────────────────────────────────────────────

@Serializable
data class PunchDto(
    @SerialName("id") val id: String,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("type") val type: String,
    @SerialName("device_id") val deviceId: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("created_by") val createdBy: String
)

// ──────────────────────────────────────────────
// Audit Log DTOs
// ──────────────────────────────────────────────

@Serializable
data class AuditLogDto(
    @SerialName("id") val id: String,
    @SerialName("entity_type") val entityType: String,
    @SerialName("entity_id") val entityId: String,
    @SerialName("action") val action: String,
    @SerialName("performed_by") val performedBy: String,
    @SerialName("old_value") val oldValue: String? = null,
    @SerialName("new_value") val newValue: String? = null,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("reason") val reason: String? = null
)
