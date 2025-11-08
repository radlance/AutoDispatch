package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceMobile
import com.github.radlance.autodispatch.common.data.KtorApiServiceMobile
import com.github.radlance.autodispatch.request.data.RemoteRequestRepository
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.request.presentation.RequestViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val requestModule
    get() = module {
        singleOf(::KtorApiServiceMobile).bind<ApiServiceMobile>()
        singleOf(::RemoteRequestRepository).bind<RequestRepository>()
        viewModelOf(::RequestViewModel)
    }