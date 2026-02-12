package com.merchpulse.core.database

import com.merchpulse.shared.domain.model.*
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.ProductRepository
import kotlinx.datetime.Clock
import java.util.UUID

class DataSeeder(
    private val productRepository: ProductRepository,
    private val employeeRepository: EmployeeRepository
) {
    suspend fun seed() {
        // Seed Admin
        val admin = Employee(
            id = "admin-1",
            email = "admin@merchpulse.com",
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
    }
}
