package com.github.radlance.autodispatch.di

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.KtorApiServiceJvm
import com.github.radlance.autodispatch.controlpanel.data.RemoteControlPanelRepository
import com.github.radlance.autodispatch.controlpanel.domain.ControlPanelRepository
import com.github.radlance.autodispatch.controlpanel.presentation.ControlPanelViewModel
import com.github.radlance.autodispatch.driver.assignment.data.RemoteVehicleAssignmentRepository
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.driver.assignment.presentation.VehicleAssignmentViewModel
import com.github.radlance.autodispatch.driver.core.data.RemoteDriverRepository
import com.github.radlance.autodispatch.driver.core.domain.DriverRepository
import com.github.radlance.autodispatch.driver.core.presentation.DriverViewModel
import com.github.radlance.autodispatch.driver.history.data.RemoteDriverHistoryRepository
import com.github.radlance.autodispatch.driver.history.domain.DriverHistoryRepository
import com.github.radlance.autodispatch.driver.history.presentation.DriverHistoryViewModel
import com.github.radlance.autodispatch.driver.request.data.RemoteDriverRequestRepository
import com.github.radlance.autodispatch.driver.request.domain.DriverRequestRepository
import com.github.radlance.autodispatch.driver.request.presentation.DriverRequestAssignmentViewModel
import com.github.radlance.autodispatch.driver.unassignment.data.RemoteVehicleUnassignmentRepository
import com.github.radlance.autodispatch.driver.unassignment.domain.VehicleUnassignmentRepository
import com.github.radlance.autodispatch.driver.unassignment.presentation.VehicleUnassignmentViewModel
import com.github.radlance.autodispatch.request.assignment.data.RemoteDriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.presentation.DriverAssignmentViewModel
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
import com.github.radlance.autodispatch.request.core.presentation.RequestViewModel
import com.github.radlance.autodispatch.vehicle.assignment.data.RemoteDriverVehicleAssignmentRepository
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverVehicleAssignmentRepository
import com.github.radlance.autodispatch.vehicle.assignment.presentation.DriverVehicleAssignmentViewModel
import com.github.radlance.autodispatch.vehicle.core.data.RemoteVehicleRepository
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleRepository
import com.github.radlance.autodispatch.vehicle.core.presentation.VehicleViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val controlPanelModule
    get() = module {
        singleOf(::RemoteControlPanelRepository).bind<ControlPanelRepository>()
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
        singleOf(::RemoteDriverAssignmentRepository).bind<DriverAssignmentRepository>()
        viewModelOf(::DriverAssignmentViewModel)
    }

val pointSelectionModule
    get() = module {
        singleOf(::RemotePointSelectionRepository).bind<PointSelectionRepository>()
        viewModelOf(::PointSelectionViewModel)
    }

val driverModule
    get() = module {
        singleOf(::RemoteDriverRepository).bind<DriverRepository>()
        viewModelOf(::DriverViewModel)
    }

val vehicleAssignmentModule
    get() = module {
        singleOf(::RemoteVehicleAssignmentRepository).bind<VehicleAssignmentRepository>()
        viewModelOf(::VehicleAssignmentViewModel)
    }

val driverHistoryModule
    get() = module {
        singleOf(::RemoteDriverHistoryRepository).bind<DriverHistoryRepository>()
        viewModelOf(::DriverHistoryViewModel)
    }

val driveRequestModule
    get() = module {
        singleOf(::RemoteDriverRequestRepository).bind<DriverRequestRepository>()
        viewModelOf(::DriverRequestAssignmentViewModel)
    }

val vehicleModule
    get() = module {
        singleOf(::RemoteVehicleRepository).bind<VehicleRepository>()
        viewModelOf(::VehicleViewModel)
    }

val driverVehicleModule
    get() = module {
        singleOf(::RemoteDriverVehicleAssignmentRepository).bind<DriverVehicleAssignmentRepository>()
        viewModelOf(::DriverVehicleAssignmentViewModel)
    }

val vehicleUnassignmentModule
    get() = module {
        singleOf(::RemoteVehicleUnassignmentRepository).bind<VehicleUnassignmentRepository>()
        viewModelOf(::VehicleUnassignmentViewModel)
    }