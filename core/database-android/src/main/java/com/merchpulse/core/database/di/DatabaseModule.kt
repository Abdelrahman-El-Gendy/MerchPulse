package com.merchpulse.core.database.di

import com.merchpulse.core.database.AppDatabase
import com.merchpulse.core.database.DataSeeder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.build(androidContext()) }
    single { get<AppDatabase>().productDao() }
    single { get<AppDatabase>().employeeDao() }
    single { get<AppDatabase>().punchDao() }
    single { get<AppDatabase>().auditDao() }
    single { DataSeeder(get(), get()) }
}
