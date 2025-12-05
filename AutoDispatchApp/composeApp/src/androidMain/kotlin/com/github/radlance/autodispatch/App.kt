package com.github.radlance.autodispatch

import android.app.Application
import com.github.radlance.autodispatch.di.authModule
import com.github.radlance.autodispatch.di.commonModule
import com.github.radlance.autodispatch.di.dataStoreModule
import com.github.radlance.autodispatch.di.deliveryHistoryModule
import com.github.radlance.autodispatch.di.deliveryModule
import com.github.radlance.autodispatch.di.deliveryPlatformModule
import com.github.radlance.autodispatch.di.navigationModule
import com.github.radlance.autodispatch.di.profileModule
import com.github.radlance.autodispatch.di.requestModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataStoreModule,
                commonModule,
                navigationModule,
                authModule,
                profileModule,
                requestModule,
                deliveryModule,
                deliveryPlatformModule,
                deliveryHistoryModule
            )
        }
    }
}