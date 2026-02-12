package com.merchpulse.shared.data.remote

import com.merchpulse.shared.domain.model.Product

interface RemoteProductDataSource {
    suspend fun fetchProducts(): List<Product>
}

class FakeRemoteProductDataSource : RemoteProductDataSource {
    override suspend fun fetchProducts(): List<Product> = emptyList() // Return empty or mock
}
