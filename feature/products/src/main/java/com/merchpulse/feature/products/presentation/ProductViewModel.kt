package com.merchpulse.feature.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.feature.products.ProductsEffect
import com.merchpulse.shared.feature.products.ProductsIntent
import com.merchpulse.shared.feature.products.ProductsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel(
    private val repository: ProductRepository,
    private val authPolicy: com.merchpulse.shared.domain.policy.AuthorizationPolicy,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(ProductsIntent.LoadProducts)
    }

    fun handleIntent(intent: ProductsIntent) {
        viewModelScope.launch(dispatcherProvider.io) {
            when (intent) {
                is ProductsIntent.LoadProducts -> loadProducts()
                is ProductsIntent.ToggleLowStockFilter -> toggleLowStock(intent.show)
                is ProductsIntent.SearchProducts -> searchProducts(intent.query)
                is ProductsIntent.DeleteProduct -> deleteProduct(intent.id)
                is ProductsIntent.AddProduct -> addProduct(intent.product)
                is ProductsIntent.FilterByStatus -> filterByStatus(intent.status)
            }
        }
    }

    private suspend fun filterByStatus(status: com.merchpulse.shared.domain.model.ProductStatus?) {
        withContext(dispatcherProvider.main) {
            _state.value = _state.value.copy(statusFilter = status)
        }
        loadProducts()
    }

    private suspend fun loadProducts() {
        withContext(dispatcherProvider.main) { _state.value = _state.value.copy(isLoading = true) }
        try {
            repository.getAllProducts().collect { list ->
                // Apply filters if needed locally or re-query
                val filtered = if (_state.value.showLowStockOnly) {
                    list.filter { it.stockQty <= it.lowStockThreshold }
                } else {
                    list
                }
                withContext(dispatcherProvider.main) {
                    _state.value = _state.value.copy(products = filtered, isLoading = false)
                }
            }
        } catch (e: Exception) {
            withContext(dispatcherProvider.main) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun toggleLowStock(show: Boolean) {
        withContext(dispatcherProvider.main) { _state.value = _state.value.copy(showLowStockOnly = show) }
        // Reload to apply filter (or just filter current list if we kept full list)
        loadProducts()
    }
    
    private suspend fun searchProducts(query: String) {
        // Debounce could be added here
        withContext(dispatcherProvider.main) { _state.value = _state.value.copy(isLoading = true) }
        repository.searchProducts(query).collect { list ->
            withContext(dispatcherProvider.main) {
                 _state.value = _state.value.copy(products = list, isLoading = false)
            }
        }
    }
    
    private suspend fun deleteProduct(id: String) {
        try {
            authPolicy.requirePermission(com.merchpulse.shared.domain.model.Permission.PRODUCT_DELETE)
            val result = repository.softDeleteProduct(id)
            if (result.isSuccess) {
                _effect.send(ProductsEffect.ShowToast("Product Deleted"))
            } else {
                _effect.send(ProductsEffect.ShowToast("Delete Failed"))
            }
        } catch (e: Exception) {
            _effect.send(ProductsEffect.ShowToast(e.message ?: "Unauthorized"))
        }
    }

    private suspend fun addProduct(product: com.merchpulse.shared.domain.model.Product) {
        val result = repository.createProduct(product)
        if (result.isSuccess) {
            _effect.send(ProductsEffect.ShowToast("Product Added"))
        } else {
            _effect.send(ProductsEffect.ShowToast("Add Failed"))
        }
    }
}
