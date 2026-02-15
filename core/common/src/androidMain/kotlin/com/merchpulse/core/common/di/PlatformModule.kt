package com.merchpulse.core.common.di

import com.merchpulse.core.common.AndroidPreferencesManager
import com.merchpulse.core.common.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {
    single<PreferencesManager> { AndroidPreferencesManager(androidContext()) }
}
