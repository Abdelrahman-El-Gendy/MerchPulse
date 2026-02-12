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

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val dispatcherProvider: DispatcherProvider
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductById(id: String): Flow<Product?> {
        return productDao.getProductById(id).map { it?.toDomain() }
    }
    
    override fun getProductsByStatus(status: ProductStatus): Flow<List<Product>> {
        return productDao.getProductsByStatus(status.name).map { list -> list.map { it.toDomain() } }
    }
    
    override fun getLowStockProducts(threshold: Int): Flow<List<Product>> {
        return productDao.getLowStockProducts().map { list -> list.map { it.toDomain() } }
    }
    
    override fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query).map { list -> list.map { it.toDomain() } }
    }
    
    override suspend fun createProduct(product: Product): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            productDao.insertProduct(product.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProduct(product: Product): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            productDao.updateProduct(product.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun softDeleteProduct(id: String): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            productDao.softDeleteProduct(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLowStockCount(): Flow<Int> = productDao.getLowStockCount()

    override fun getUpcomingCount(): Flow<Int> = productDao.getUpcomingCount()

    override suspend fun adjustStock(id: String, newQty: Int): Result<Unit> = withContext(dispatcherProvider.io) {
        try {
            productDao.adjustStock(id, newQty, System.currentTimeMillis())
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

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
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
}
