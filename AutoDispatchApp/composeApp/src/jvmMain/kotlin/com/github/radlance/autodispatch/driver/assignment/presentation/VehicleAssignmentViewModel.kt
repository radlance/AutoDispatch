package com.github.radlance.autodispatch.driver.assignment.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.driver.common.presentation.SearchPaginatedViewModel
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleAssignmentViewModel(
    private val repository: VehicleAssignmentRepository
) : SearchPaginatedViewModel<Vehicle>(
    pageSize = 5
) {
    private val assignDriverStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, DeliveryError>>(FetchResultUiState.Idle)
    val assignDriverState = assignDriverStateMutable.asStateFlow()

    fun assignVehicle(
        vehicleId: Int,
        driverId: Int,
        reassign: Boolean
    ) {
        assignDriverStateMutable.value = FetchResultUiState.Loading
        handle(
            background = {
                if (reassign) {
                    repository.reassignVehicleToDriver(vehicleId, driverId)
                } else {
                    repository.assignVehicleToDriver(vehicleId, driverId)
                }
            }
        ) {
            assignDriverStateMutable.value = it.toUiState()
        }
    }

    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<Vehicle>, String> {
        return repository.vehicleAssignments(
            page = page,
            pageSize = pageSize,
            searchQuery = query
        )
    }

    override fun resetState() {
        super.resetState()
        assignDriverStateMutable.value = FetchResultUiState.Idle
    }

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}