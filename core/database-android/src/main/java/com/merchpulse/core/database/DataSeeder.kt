package com.merchpulse.core.database

import com.merchpulse.shared.domain.model.*
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.domain.repository.PunchRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours
import java.util.UUID

class DataSeeder(
    private val productRepository: ProductRepository,
    private val employeeRepository: EmployeeRepository,
    private val punchRepository: PunchRepository
) {
    suspend fun seed() {
        // Seed Admin
        val admin = Employee(
            id = "admin-1",
            email = "admin@merchpulse.com",
            phoneNumber = "1234567890",
            fullName = "System Admin",
            role = Role.ADMIN,
            permissions = Permission.entries.toSet(),
            isActive = true,
            joinedAt = Clock.System.now()
        )
        employeeRepository.createEmployee(admin, "1234")
        
        // Seed Alex Rivera (Design Mockup)
        val alex = Employee(
            id = "alex-1",
            email = "alex.rivera@merchpulse.com",
            phoneNumber = "9876543210",
            fullName = "Alex Rivera",
            role = Role.STAFF,
            permissions = setOf(Permission.PRODUCT_VIEW, Permission.PUNCH_SELF),
            isActive = true,
            joinedAt = Clock.System.now()
        )
        employeeRepository.createEmployee(alex, "1234")

        // Seed some products
        val products = listOf(
            Product(
                id = UUID.randomUUID().toString(),
                sku = "PROD-001",
                name = "Wireless Mouse",
                description = "Ergonomic 2.4GHz mouse",
                category = "Electronics",
                status = ProductStatus.ACTIVE,
                price = 25.0,
                cost = 15.0,
                currency = "USD",
                stockQty = 50,
                lowStockThreshold = 10,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                isDeleted = false
            ),
            Product(
                id = UUID.randomUUID().toString(),
                sku = "PROD-002",
                name = "Mechanical Keyboard",
                description = "RGB backlit keyboard",
                category = "Electronics",
                status = ProductStatus.ACTIVE,
                price = 85.0,
                cost = 45.0,
                currency = "USD",
                stockQty = 5,
                lowStockThreshold = 10, // LOW STOCK
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                isDeleted = false
            )
        )
        products.forEach { productRepository.createProduct(it) }

        // Seed some Punches for Alex Rivera
        val now = Clock.System.now()
        val punches = listOf(
            TimePunch(
                id = UUID.randomUUID().toString(),
                employeeId = "alex-1",
                timestamp = now - 6.hours,
                type = PunchType.IN,
                deviceId = "mock-device",
                note = "Starting shift",
                createdBy = "alex-1"
            ),
            TimePunch(
                id = UUID.randomUUID().toString(),
                employeeId = "alex-1",
                timestamp = now - 2.hours,
                type = PunchType.OUT,
                deviceId = "mock-device",
                note = "Lunch break",
                createdBy = "alex-1"
            ),
            TimePunch(
                id = UUID.randomUUID().toString(),
                employeeId = "alex-1",
                timestamp = now - 1.hours,
                type = PunchType.IN,
                deviceId = "mock-device",
                note = "Back from lunch",
                createdBy = "alex-1"
            )
        )
        punches.forEach { punchRepository.recordPunch(it) }
    }
}
