package com.merchpulse.core.supabase.mapper

import com.merchpulse.core.supabase.dto.*
import com.merchpulse.shared.domain.model.*
import kotlinx.datetime.Instant

/**
 * Mappers between Supabase DTOs and domain models.
 *
 * These functions translate network-layer data to/from the
 * domain models used throughout the app.
 */

// ──────────────────────────────────────────────
// Product Mappers
// ──────────────────────────────────────────────

fun ProductDto.toDomain(): Product = Product(
    id = id,
    sku = sku,
    name = name,
    description = description,
    category = category,
    status = ProductStatus.valueOf(status),
    price = price,
    cost = cost,
    currency = currency,
    stockQty = stockQty,
    lowStockThreshold = lowStockThreshold,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
    isDeleted = isDeleted
)

fun Product.toDto(): ProductDto = ProductDto(
    id = id,
    sku = sku,
    name = name,
    description = description,
    category = category,
    status = status.name,
    price = price,
    cost = cost,
    currency = currency,
    stockQty = stockQty,
    lowStockThreshold = lowStockThreshold,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds(),
    isDeleted = isDeleted
)

fun Product.toInsertDto(): ProductInsertDto = ProductInsertDto(
    id = id,
    sku = sku,
    name = name,
    description = description,
    category = category,
    status = status.name,
    price = price,
    cost = cost,
    currency = currency,
    stockQty = stockQty,
    lowStockThreshold = lowStockThreshold,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds(),
    isDeleted = isDeleted
)

// ──────────────────────────────────────────────
// Employee Mappers
// ──────────────────────────────────────────────

fun EmployeeDto.toDomain(permissions: List<EmployeePermissionDto>): Employee = Employee(
    id = id,
    email = email,
    phoneNumber = phoneNumber,
    fullName = fullName,
    role = Role.valueOf(role),
    permissions = permissions.map { Permission.valueOf(it.permission) }.toSet(),
    isActive = isActive,
    joinedAt = Instant.fromEpochMilliseconds(joinedAt)
)

fun Employee.toInsertDto(pinHash: String): EmployeeInsertDto = EmployeeInsertDto(
    id = id,
    email = email,
    phoneNumber = phoneNumber,
    fullName = fullName,
    role = role.name,
    isActive = isActive,
    joinedAt = joinedAt.toEpochMilliseconds(),
    pinHash = pinHash
)

fun Employee.toPermissionDtos(): List<EmployeePermissionDto> =
    permissions.map { EmployeePermissionDto(employeeId = id, permission = it.name) }

// ──────────────────────────────────────────────
// Punch Mappers
// ──────────────────────────────────────────────

fun PunchDto.toDomain(): TimePunch = TimePunch(
    id = id,
    employeeId = employeeId,
    timestamp = Instant.fromEpochMilliseconds(timestamp),
    type = PunchType.valueOf(type),
    deviceId = deviceId,
    note = note,
    createdBy = createdBy
)

fun TimePunch.toDto(): PunchDto = PunchDto(
    id = id,
    employeeId = employeeId,
    timestamp = timestamp.toEpochMilliseconds(),
    type = type.name,
    deviceId = deviceId,
    note = note,
    createdBy = createdBy
)

// ──────────────────────────────────────────────
// Audit Log Mappers
// ──────────────────────────────────────────────

fun AuditLogDto.toDomain(): AuditLog = AuditLog(
    id = id,
    entityType = entityType,
    entityId = entityId,
    action = action,
    performedBy = performedBy,
    previousState = oldValue,
    newState = newValue,
    timestamp = Instant.fromEpochMilliseconds(timestamp),
    note = reason
)

fun AuditLog.toDto(): AuditLogDto = AuditLogDto(
    id = id,
    entityType = entityType,
    entityId = entityId,
    action = action,
    performedBy = performedBy,
    oldValue = previousState,
    newValue = newState,
    timestamp = timestamp.toEpochMilliseconds(),
    reason = note
)
