package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataStoreModule: Module = module {
    single { createDataStore(androidContext()) }
}