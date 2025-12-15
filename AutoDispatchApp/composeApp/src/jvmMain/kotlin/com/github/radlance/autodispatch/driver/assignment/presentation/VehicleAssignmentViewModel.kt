package com.github.radlance.autodispatch.driver.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.driver.assignment.domain.VehicleAssignmentRepository
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VehicleAssignmentViewModel(
    private val repository: VehicleAssignmentRepository
) : BaseViewModel(), EventViewModel<VehicleAssignmentEvent> {
    private val vehicleAssignmentsStateMutable =
        MutableStateFlow<FetchResultUiState<List<Vehicle>, String>>(FetchResultUiState.Idle)
    val vehicleAssignmentsState = vehicleAssignmentsStateMutable.asStateFlow()

    private val assignDriverStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val assignDriverState = assignDriverStateMutable.asStateFlow()

    private val vehicleAssignmentFieldsStateMutable =
        MutableStateFlow(VehicleAssignmentFieldsState())
    val vehicleAssignmentFieldsState = vehicleAssignmentFieldsStateMutable.asStateFlow()

    fun loadVehicleAssignments() {
        vehicleAssignmentsStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::vehicleAssignments) {
            vehicleAssignmentsStateMutable.value = it.toUiState()
        }
    }

    override fun reduce(event: VehicleAssignmentEvent) {
        val action = object : VehicleAssignmentAction {
            override fun resetStates() {
                vehicleAssignmentFieldsStateMutable.update { state ->
                    state.copy(selectedVehicle = null)
                }
                assignDriverStateMutable.value = FetchResultUiState.Idle
            }

            override fun changeVehicle(vehicle: Vehicle) {
                vehicleAssignmentFieldsStateMutable.update { state ->
                    state.copy(selectedVehicle = vehicle)
                }
            }

            override fun assignVehicle(
                vehicleId: Int,
                driverId: Int,
                isReassign: Boolean
            ) {
                assignDriverStateMutable.value = FetchResultUiState.Loading
                handle(
                    background = {
                        repository.assignVehicleToDriver(vehicleId, driverId)
                    }
                ) {
                    assignDriverStateMutable.value = it.toUiState()
                }
            }


        }
        event.apply(action)
    }
}