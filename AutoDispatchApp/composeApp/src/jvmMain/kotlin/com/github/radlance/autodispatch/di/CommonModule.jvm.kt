package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.KtorApiServiceJvm
import com.github.radlance.autodispatch.controlpanel.presentation.ControlPanelViewModel
import com.github.radlance.autodispatch.request.assignment.data.RemoteAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.AssignmentRepository
import com.github.radlance.autodispatch.request.assignment.presentation.AssignmentViewModel
import com.github.radlance.autodispatch.request.change.data.RemoteChangeRequestRepository
import com.github.radlance.autodispatch.request.change.data.RemotePointSelectionRepository
import com.github.radlance.autodispatch.request.change.domain.ChangeRequestRepository
import com.github.radlance.autodispatch.request.change.domain.PointSelectionRepository
import com.github.radlance.autodispatch.request.change.presentation.BaseRequestValidator
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestViewModel
import com.github.radlance.autodispatch.request.change.presentation.PointSelectionViewModel
import com.github.radlance.autodispatch.request.change.presentation.RequestValidator
import com.github.radlance.autodispatch.request.core.data.RemoteRequestRepository
import com.github.radlance.autodispatch.request.core.domain.RequestRepository
import com.github.radlance.autodispatch.request.core.presentation.core.RequestViewModel
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

val changeRequestModule
    get() = module {
        singleOf(::RemoteChangeRequestRepository).bind<ChangeRequestRepository>()
        singleOf(::BaseRequestValidator).bind<RequestValidator>()
        viewModelOf(::ChangeRequestViewModel)
    }

val driverAssignmentModule
    get() = module {
        singleOf(::RemoteAssignmentRepository).bind<AssignmentRepository>()
        viewModelOf(::AssignmentViewModel)
    }

val pointSelectionModule
    get() = module {
        singleOf(::RemotePointSelectionRepository).bind<PointSelectionRepository>()
        viewModelOf(::PointSelectionViewModel)
    }