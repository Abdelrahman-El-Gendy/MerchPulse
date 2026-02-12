package com.merchpulse.feature.stock.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.shared.domain.model.Permission
import com.merchpulse.shared.domain.policy.AuthorizationPolicy
import com.merchpulse.shared.domain.repository.ProductRepository
import com.merchpulse.shared.feature.stock.StockEffect
import com.merchpulse.shared.feature.stock.StockIntent
import com.merchpulse.shared.feature.stock.StockState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LowStockViewModel(
    private val repository: ProductRepository,
    private val auditRepository: com.merchpulse.shared.domain.repository.AuditRepository,
    private val authPolicy: AuthorizationPolicy,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(StockState())
    val state = _state.asStateFlow()

    private val _effect = Channel<StockEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        handleIntent(StockIntent.LoadLowStock)
    }

    fun handleIntent(intent: StockIntent) {
        when (intent) {
            is StockIntent.LoadLowStock -> loadLowStock()
            is StockIntent.AdjustStock -> adjustStock(intent.productId, intent.newQty)
        }
    }

    private fun loadLowStock() {
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            try {
                authPolicy.requirePermission(Permission.PRODUCT_VIEW)
                repository.getLowStockProducts().collect { list ->
                    _state.update { it.copy(lowStockProducts = list, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun adjustStock(id: String, qty: Int) {
        viewModelScope.launch(dispatcherProvider.io) {
            try {
                authPolicy.requirePermission(Permission.STOCK_ADJUST)
                val result = repository.adjustStock(id, qty)
                if (result.isSuccess) {
                    auditRepository.logAction(
                        action = "STOCK_ADJUST",
                        entityType = "PRODUCT",
                        entityId = id,
                        previousState = null, // simplified 
                        newState = "qty: $qty"
                    )
                    _effect.send(StockEffect.ShowMessage("Stock adjusted successfully"))
                } else {
                    _effect.send(StockEffect.ShowMessage("Adjustment failed"))
                }
            } catch (e: Exception) {
                _effect.send(StockEffect.ShowMessage(e.message ?: "Action unauthorized"))
            }
        }
    }
}
