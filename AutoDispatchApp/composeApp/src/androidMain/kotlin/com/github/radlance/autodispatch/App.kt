package com.github.radlance.autodispatch

import android.app.Application
import com.github.radlance.autodispatch.di.initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
        }
    }
}