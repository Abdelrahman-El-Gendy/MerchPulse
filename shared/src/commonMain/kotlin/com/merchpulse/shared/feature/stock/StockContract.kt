package com.merchpulse.shared.feature.stock

import com.merchpulse.shared.domain.model.Product
import com.merchpulse.shared.mvi.UiEffect
import com.merchpulse.shared.mvi.UiIntent
import com.merchpulse.shared.mvi.UiState

data class StockState(
    val lowStockProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class StockIntent : UiIntent {
    data object LoadLowStock : StockIntent()
    data class AdjustStock(val productId: String, val newQty: Int) : StockIntent()
}

sealed class StockEffect : UiEffect {
    data class ShowMessage(val message: String) : StockEffect()
}
