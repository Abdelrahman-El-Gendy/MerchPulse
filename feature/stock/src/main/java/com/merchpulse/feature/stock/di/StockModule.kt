package com.merchpulse.feature.stock.di

import com.merchpulse.feature.stock.presentation.LowStockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val stockModule = module {
    viewModel { LowStockViewModel(get(), get(), get(), get()) }
}
