package com.merchpulse.core.supabase.datasource

import com.merchpulse.core.supabase.dto.*
import com.merchpulse.core.supabase.mapper.*
import com.merchpulse.shared.domain.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Remote data source for Products backed by Supabase Postgrest.
 */
interface SupabaseProductDataSource {
    suspend fun fetchAllProducts(): List<Product>
    suspend fun fetchProductById(id: String): Product?
    suspend fun fetchLowStockProducts(threshold: Int? = null): List<Product>
    suspend fun upsertProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(id: String): Result<Unit>
    suspend fun updateStockQty(id: String, newQty: Int): Result<Unit>
}

class SupabaseProductDataSourceImpl(
    private val client: SupabaseClient
) : SupabaseProductDataSource {

    companion object {
        private const val TABLE = "products"
    }

    override suspend fun fetchAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select()
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun fetchProductById(id: String): Product? = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE]
                .select {
                    filter { eq("id", id) }
                }
                .decodeSingleOrNull<ProductDto>()
                ?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchLowStockProducts(threshold: Int?): List<Product> = withContext(Dispatchers.IO) {
        client.postgrest[TABLE]
            .select {
                filter {
                    eq("is_deleted", false)
                }
            }
            .decodeList<ProductDto>()
            .filter { if (threshold != null) it.stockQty <= threshold else it.stockQty <= it.lowStockThreshold }
            .map { it.toDomain() }
    }

    override suspend fun upsertProduct(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE].upsert(product.toInsertDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Soft delete
            client.postgrest[TABLE].update({
                set("is_deleted", true)
            }) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStockQty(id: String, newQty: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest[TABLE].update({
                set("stock_qty", newQty)
                set("updated_at", kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
            }) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
