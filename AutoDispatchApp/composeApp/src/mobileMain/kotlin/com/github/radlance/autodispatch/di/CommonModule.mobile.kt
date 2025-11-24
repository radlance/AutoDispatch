package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.KtorApiServiceMobile
import com.github.radlance.autodispatch.delivery.core.data.RemoteDeliveryRepository
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryViewModel
import com.github.radlance.autodispatch.delivery.details.data.RemoteDeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailsRepository
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import com.github.radlance.autodispatch.delivery.route.presentation.DeliveryRouteViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val requestModule
    get() = module {
        singleOf(::KtorApiServiceMobile).bind<ApiServiceMobile>()
    }

val deliveryModule
    get() = module {
        singleOf(::RemoteDeliveryRepository).bind<DeliveryRepository>()
        viewModelOf(::DeliveryViewModel)
        singleOf(::RemoteDeliveryDetailsRepository).bind<DeliveryDetailsRepository>()
        viewModelOf(::DeliveryDetailsViewModel)
        viewModelOf(::DeliveryRouteViewModel)
    }