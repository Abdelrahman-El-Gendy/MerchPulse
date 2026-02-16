package com.merchpulse.feature.auth.di

import com.merchpulse.feature.auth.data.EmployeeRepositoryImpl
import com.merchpulse.feature.auth.data.SessionManagerImpl
import com.merchpulse.feature.auth.presentation.SignInViewModel
import com.merchpulse.feature.auth.presentation.SignUpViewModel
import com.merchpulse.shared.domain.policy.AuthorizationPolicy
import com.merchpulse.shared.domain.repository.EmployeeRepository
import com.merchpulse.shared.domain.repository.SessionManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<SessionManager> { SessionManagerImpl(get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get(), get()) }
    single<com.merchpulse.shared.domain.repository.AuditRepository> { com.merchpulse.feature.auth.data.AuditRepositoryImpl(get(), get(), get()) }
    single { AuthorizationPolicy(get()) }
    
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
}
