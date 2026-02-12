package com.merchpulse.feature.products

import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.feature.products.presentation.ProductViewModel
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.feature.products.ProductsIntent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {
    private val repository = mockk<ProductRepository>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val dispatcherProvider = object : DispatcherProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `LoadProducts intent updates state with products`() = runTest {
        val mockProducts = listOf(
            Product(
                id = "1", sku = "S1", name = "P1", description = null, category = null,
                status = ProductStatus.ACTIVE, price = 10.0, cost = null, currency = "USD",
                stockQty = 10, lowStockThreshold = 2, createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(), isDeleted = false
            )
        )
        every { repository.getAllProducts() } returns flowOf(mockProducts)

        val viewModel = ProductViewModel(repository, dispatcherProvider)
        viewModel.handleIntent(ProductsIntent.LoadProducts)

        assertEquals(mockProducts, viewModel.state.value.products)
        assertEquals(false, viewModel.state.value.isLoading)
    }
}
