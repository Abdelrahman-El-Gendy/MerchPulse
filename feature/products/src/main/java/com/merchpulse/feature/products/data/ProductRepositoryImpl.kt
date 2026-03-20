package com.merchpulse.feature.products.data

import com.merchpulse.core.database.dao.ProductDao
import com.merchpulse.core.database.entity.ProductEntity
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import com.merchpulse.core.common.DispatcherProvider
import kotlinx.coroutines.withContext

import com.merchpulse.shared.domain.repository.SessionManager
import kotlinx.coroutines.flow.emptyFlow

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val sessionManager: SessionManager,
    private val dispatcherProvider: DispatcherProvider
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getAllProducts(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductById(id: String): Flow<Product?> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getProductById(userId, id).map { it?.toDomain() }
    }
    
    override fun getProductsByStatus(status: ProductStatus): Flow<List<Product>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getProductsByStatus(userId, status.name).map { list -> list.map { it.toDomain() } }
    }
    
    override fun getLowStockProducts(threshold: Int): Flow<List<Product>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getLowStockProducts(userId).map { list -> list.map { it.toDomain() } }
    }
    
    override fun searchProducts(query: String): Flow<List<Product>> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.searchProducts(userId, query).map { list -> list.map { it.toDomain() } }
    }
    
    override suspend fun createProduct(product: Product): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            productDao.insertProduct(product.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProduct(product: Product): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            productDao.updateProduct(product.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun softDeleteProduct(id: String): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            productDao.softDeleteProduct(userId, id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLowStockCount(): Flow<Int> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getLowStockCount(userId)
    }

    override fun getUpcomingCount(): Flow<Int> {
        val userId = sessionManager.currentUserId ?: return emptyFlow()
        return productDao.getUpcomingCount(userId)
    }

    override suspend fun adjustStock(id: String, newQty: Int): Result<Unit> = withContext(dispatcherProvider.io) {
        val userId = sessionManager.currentUserId ?: return@withContext Result.failure(Exception("No user logged in"))
        try {
            productDao.adjustStock(userId, id, newQty, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun ProductEntity.toDomain(): Product {
    return Product(
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
}

fun Product.toEntity(ownerUserId: String): ProductEntity {
    return ProductEntity(
        id = id,
        ownerUserId = ownerUserId,
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
}
