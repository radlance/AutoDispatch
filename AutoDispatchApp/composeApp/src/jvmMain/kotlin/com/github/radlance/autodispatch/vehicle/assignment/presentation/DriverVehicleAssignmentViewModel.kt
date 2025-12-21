package com.github.radlance.autodispatch.vehicle.assignment.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.driver.common.presentation.SearchPaginatedViewModel
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverVehicleAssignmentRepository
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverWithoutVehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverVehicleAssignmentViewModel(
    private val driverRepository: DriverVehicleAssignmentRepository,
    private val vehicleRepository: VehicleAssignmentRepository
) : SearchPaginatedViewModel<DriverWithoutVehicle>(pageSize = 8) {
    private val assignVehicleStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val assignVehicleState = assignVehicleStateMutable.asStateFlow()

    fun assignVehicle(
        vehicleId: Int,
        driverId: Int
    ) {
        assignVehicleStateMutable.value = FetchResultUiState.Loading
        handle(
            background = {
                vehicleRepository.assignVehicleToDriver(vehicleId, driverId)
            }
        ) {
            assignVehicleStateMutable.value = it.toUiState()
        }
    }

    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverWithoutVehicle>, String> {
        return driverRepository.driversWithoutVehicle(
            page = page,
            pageSize = pageSize,
            searchQuery = query
        )
    }

    override fun resetState() {
        super.resetState()
        assignVehicleStateMutable.value = FetchResultUiState.Idle
    }

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}