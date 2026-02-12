package com.merchpulse.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.merchpulse.core.database.dao.ProductDao
import com.merchpulse.core.database.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetProduct() = runBlocking {
        val product = ProductEntity(
            id = "1", sku = "SKU1", name = "Test", description = "", category = "",
            status = "ACTIVE", price = 10.0, cost = 5.0, currency = "USD",
            stockQty = 10, lowStockThreshold = 2, createdAt = 0L, updatedAt = 0L, isDeleted = false
        )
        dao.insertProduct(product)
        
        val result = dao.getAllProducts().first()
        assertEquals(1, result.size)
        assertEquals("Test", result[0].name)
    }
}
