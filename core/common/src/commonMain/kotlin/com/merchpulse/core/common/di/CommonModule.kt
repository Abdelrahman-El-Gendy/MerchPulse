package com.merchpulse.core.common.di

import com.merchpulse.core.common.DispatcherProvider
import com.merchpulse.core.common.StandardDispatchers
import org.koin.dsl.module

val commonModule = module {
    single<DispatcherProvider> { StandardDispatchers() }
}
