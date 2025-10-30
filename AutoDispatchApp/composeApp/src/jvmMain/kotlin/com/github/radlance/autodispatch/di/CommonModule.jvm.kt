package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.KtorApiServiceJvm
import com.github.radlance.autodispatch.controlpanel.presentation.ControlPanelViewModel
import com.github.radlance.autodispatch.request.data.RemoteRequestRepository
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.request.presentation.core.RequestViewModel
import com.github.radlance.autodispatch.request.presentation.create.CreateRequestViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val controlPanelModule
    get() = module {
        viewModelOf(::ControlPanelViewModel)
    }

val requestModule
    get() = module {
        singleOf(::KtorApiServiceJvm).bind<ApiServiceJvm>()
        singleOf(::RemoteRequestRepository).bind<RequestRepository>()
        singleOf(::RequestViewModel)
    }

val createRequestModule
    get() = module {
        viewModelOf(::CreateRequestViewModel)
    }