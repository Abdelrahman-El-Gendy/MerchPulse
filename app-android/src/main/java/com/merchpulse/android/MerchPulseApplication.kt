package com.merchpulse.android

import android.app.Application
import com.merchpulse.core.common.di.commonModule
import com.merchpulse.core.database.DataSeeder
import com.merchpulse.core.database.di.databaseModule
import com.merchpulse.feature.auth.di.authModule
import com.merchpulse.feature.employees.di.employeesModule
import com.merchpulse.feature.home.di.homeModule
import com.merchpulse.feature.products.di.productsModule
import com.merchpulse.feature.punching.di.punchingModule
import com.merchpulse.feature.stock.di.stockModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MerchPulseApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MerchPulseApplication)
            modules(
                commonModule,
                databaseModule,
                authModule,
                homeModule,
                productsModule,
                punchingModule,
                employeesModule,
                stockModule
            )
        }

        // Quick seed for demo
        seedData()
    }

    private fun seedData() {
        applicationScope.launch {
            val seeder: DataSeeder = get()
            seeder.seed()
        }
    }
}
