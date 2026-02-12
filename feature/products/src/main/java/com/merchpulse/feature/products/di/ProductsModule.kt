package com.merchpulse.feature.products.di

import com.merchpulse.feature.products.data.ProductRepositoryImpl
import com.merchpulse.feature.products.presentation.ProductFormViewModel
import com.merchpulse.feature.products.presentation.ProductViewModel
import com.merchpulse.shared.domain.repository.ProductRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val productsModule = module {
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { ProductFormViewModel(get(), get(), get(), get()) }
}
