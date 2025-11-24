package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.delivery.route.data.LocalDeliveryRouteRepository
import com.github.radlance.autodispatch.delivery.route.domain.DeliveryRouteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val deliveryPlatformModule: Module = module {
    single {
        LocalDeliveryRouteRepository(androidContext())
    }.bind<DeliveryRouteRepository>()
}