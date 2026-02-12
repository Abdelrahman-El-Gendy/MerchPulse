package com.merchpulse.feature.punching.di

import com.merchpulse.feature.punching.data.PunchRepositoryImpl
import com.merchpulse.feature.punching.presentation.PunchViewModel
import com.merchpulse.feature.punching.presentation.TeamPunchViewModel
import com.merchpulse.shared.domain.repository.PunchRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val punchingModule = module {
    single<PunchRepository> { PunchRepositoryImpl(get(), get()) }
    
    viewModel { PunchViewModel(get(), get(), get()) }
    viewModel { TeamPunchViewModel(get(), get(), get(), get(), get()) }
}
