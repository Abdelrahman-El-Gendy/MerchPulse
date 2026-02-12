package com.merchpulse.feature.employees.di

import com.merchpulse.feature.employees.presentation.EmployeesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val employeesModule = module {
    viewModel { EmployeesViewModel(get(), get(), get()) }
}
