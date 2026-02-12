package com.merchpulse.feature.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.domain.model.ProductStatus
import com.merchpulse.shared.domain.policy.AuthorizationPolicy
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.feature.products.ProductFormEffect
import com.merchpulse.shared.feature.products.ProductFormIntent
import com.merchpulse.shared.feature.products.ProductFormState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID

class ProductFormViewModel(
    private val repository: ProductRepository,
    private val auditRepository: com.merchpulse.shared.domain.repository.AuditRepository,
    private val authPolicy: AuthorizationPolicy,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(ProductFormState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProductFormEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: ProductFormIntent) {
        when (intent) {
            is ProductFormIntent.LoadProduct -> loadProduct(intent.id)
            is ProductFormIntent.SkuChanged -> _state.update { it.copy(sku = intent.sku) }
            is ProductFormIntent.NameChanged -> _state.update { it.copy(name = intent.name) }
            is ProductFormIntent.DescriptionChanged -> _state.update { it.copy(description = intent.desc) }
            is ProductFormIntent.CategoryChanged -> _state.update { it.copy(category = intent.cat) }
            is ProductFormIntent.StatusChanged -> _state.update { it.copy(status = intent.status) }
            is ProductFormIntent.PriceChanged -> _state.update { it.copy(price = intent.price) }
            is ProductFormIntent.CostChanged -> _state.update { it.copy(cost = intent.cost) }
            is ProductFormIntent.StockQtyChanged -> _state.update { it.copy(stockQty = intent.qty) }
            is ProductFormIntent.ThresholdChanged -> _state.update { it.copy(lowStockThreshold = intent.threshold) }
            is ProductFormIntent.IncrementStock -> _state.update { it.copy(stockQty = (it.stockQty.toIntOrNull() ?: 0).plus(1).toString()) }
            is ProductFormIntent.DecrementStock -> _state.update { it.copy(stockQty = (it.stockQty.toIntOrNull() ?: 0).minus(1).coerceAtLeast(0).toString()) }
            is ProductFormIntent.AuditReasonChanged -> _state.update { it.copy(auditReason = intent.reason) }
            is ProductFormIntent.AdjustmentNoteChanged -> _state.update { it.copy(adjustmentNote = intent.note) }
            is ProductFormIntent.Save -> saveProduct()
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getProductById(id).first()?.let { p ->
                _state.update { 
                    it.copy(
                        product = p,
                        sku = p.sku,
                        name = p.name,
                        description = p.description ?: "",
                        category = p.category ?: "",
                        status = p.status,
                        price = p.price.toString(),
                        cost = p.cost?.toString() ?: "",
                        currency = p.currency,
                        stockQty = p.stockQty.toString(),
                        lowStockThreshold = p.lowStockThreshold.toString(),
                        isEditing = true,
                        isLoading = false
                    )
                }
            } ?: _state.update { it.copy(isLoading = false, error = "Product not found") }
        }
    }

    private fun saveProduct() {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                // Check Permission
                val perm = if (_state.value.isEditing) Permission.PRODUCT_EDIT else Permission.PRODUCT_CREATE
                authPolicy.requirePermission(perm)

                val s = _state.value
                val p = Product(
                    id = s.product?.id ?: UUID.randomUUID().toString(),
                    sku = s.sku,
                    name = s.name,
                    description = s.description,
                    category = s.category,
                    status = s.status,
                    price = s.price.toDoubleOrNull() ?: 0.0,
                    cost = s.cost.toDoubleOrNull(),
                    currency = s.currency,
                    stockQty = s.stockQty.toIntOrNull() ?: 0,
                    lowStockThreshold = s.lowStockThreshold.toIntOrNull() ?: 5,
                    createdAt = s.product?.createdAt ?: Clock.System.now(),
                    updatedAt = Clock.System.now(),
                    isDeleted = false
                )

                val result = if (s.isEditing) repository.updateProduct(p) else repository.createProduct(p)
                
                if (result.isSuccess) {
                    auditRepository.logAction(
                        action = if (s.isEditing) "PRODUCT_UPDATE_${s.auditReason.name}" else "PRODUCT_CREATE",
                        entityType = "PRODUCT",
                        entityId = p.id,
                        previousState = s.product?.toString(),
                        newState = p.toString(),
                        note = if (s.isEditing) s.adjustmentNote else null
                    )
                    _effect.send(ProductFormEffect.ShowSuccess("Product saved"))
                    _effect.send(ProductFormEffect.NavigateBack)
                } else {
                    _effect.send(ProductFormEffect.ShowError("Save failed"))
                }
            } catch (e: Exception) {
                _effect.send(ProductFormEffect.ShowError(e.message ?: "Action unauthorized"))
            }
        }
    }
}
