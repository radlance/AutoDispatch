package com.github.radlance.autodispatch.driver.unassignment.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.driver.unassignment.domain.VehicleUnassignmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleUnassignmentViewModel(
    private val repository: VehicleUnassignmentRepository
) : BaseViewModel() {

    private val unassignmentStateMutable = MutableStateFlow<FetchResultUiState<Unit, RequestError>>(
        FetchResultUiState.Idle
    )
    val unassignmentState = unassignmentStateMutable.asStateFlow()

    fun unassignVehicle(driverId: Int) {
        unassignmentStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.unassignVehicle(driverId) }) {
            unassignmentStateMutable.value = it.toUiState()
        }
    }

    fun resetState() {
        unassignmentStateMutable.value = FetchResultUiState.Idle
    }
}