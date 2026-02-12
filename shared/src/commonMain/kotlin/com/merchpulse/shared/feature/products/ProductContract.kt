package com.merchpulse.shared.feature.products

import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

// ─── Product List ───────────────────────────────────────────

enum class AuditReason {
    RESTOCK, CORRECTION, DAMAGED, RETURN
}

data class ProductsState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showLowStockOnly: Boolean = false,
    val searchQuery: String = "",
    val statusFilter: ProductStatus? = null
) : UiState

sealed class ProductsIntent : UiIntent {
    data object LoadProducts : ProductsIntent()
    data class ToggleLowStockFilter(val show: Boolean) : ProductsIntent()
    data class FilterByStatus(val status: ProductStatus?) : ProductsIntent()
    data class SearchProducts(val query: String) : ProductsIntent()
    data class DeleteProduct(val id: String) : ProductsIntent()
    data class AddProduct(val product: Product) : ProductsIntent()
}

sealed class ProductsEffect : UiEffect {
    data class ShowToast(val message: String) : ProductsEffect()
    data class NavigateToProductDetail(val productId: String) : ProductsEffect()
}

// ─── Product Detail / Create / Edit ─────────────────────────

data class ProductFormState(
    val product: Product? = null,
    val sku: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val status: ProductStatus = ProductStatus.ACTIVE,
    val price: String = "",
    val cost: String = "",
    val currency: String = "USD",
    val stockQty: String = "0",
    val lowStockThreshold: String = "5",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val auditReason: AuditReason = AuditReason.RESTOCK,
    val adjustmentNote: String = ""
) : UiState

sealed class ProductFormIntent : UiIntent {
    data class LoadProduct(val id: String) : ProductFormIntent()
    data class SkuChanged(val sku: String) : ProductFormIntent()
    data class NameChanged(val name: String) : ProductFormIntent()
    data class DescriptionChanged(val desc: String) : ProductFormIntent()
    data class CategoryChanged(val cat: String) : ProductFormIntent()
    data class StatusChanged(val status: ProductStatus) : ProductFormIntent()
    data class PriceChanged(val price: String) : ProductFormIntent()
    data class CostChanged(val cost: String) : ProductFormIntent()
    data class StockQtyChanged(val qty: String) : ProductFormIntent()
    data class ThresholdChanged(val threshold: String) : ProductFormIntent()
    data object IncrementStock : ProductFormIntent()
    data object DecrementStock : ProductFormIntent()
    data class AuditReasonChanged(val reason: AuditReason) : ProductFormIntent()
    data class AdjustmentNoteChanged(val note: String) : ProductFormIntent()
    data object Save : ProductFormIntent()
}

sealed class ProductFormEffect : UiEffect {
    data object NavigateBack : ProductFormEffect()
    data class ShowError(val message: String) : ProductFormEffect()
    data class ShowSuccess(val message: String) : ProductFormEffect()
}
