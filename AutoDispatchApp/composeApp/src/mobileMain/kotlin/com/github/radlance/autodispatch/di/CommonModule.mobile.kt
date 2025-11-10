package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.KtorApiServiceMobile
import com.github.radlance.autodispatch.delivery.core.data.RemoteDeliveryRepository
import com.github.radlance.autodispatch.delivery.core.domain.DeliveryRepository
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val requestModule
    get() = module {
        singleOf(::KtorApiServiceMobile).bind<ApiServiceMobile>()
        singleOf(::RemoteDeliveryRepository).bind<DeliveryRepository>()
        viewModelOf(::DeliveryViewModel)
    }