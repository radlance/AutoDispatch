package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DriverAssignmentViewModel(
    private val repository: DriverAssignmentRepository
) : BaseViewModel(), EventViewModel<DriverAssignmentEvent> {

    private val driverAssignmentsStateMutable =
        MutableStateFlow<FetchResultUiState<List<DriverStats>, String>>(FetchResultUiState.Idle)
    val driverAssignmentsState = driverAssignmentsStateMutable.asStateFlow()

    private val assignRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, DeliveryError>>(FetchResultUiState.Idle)
    val assignRequestState = assignRequestStateMutable.asStateFlow()

    private val driverAssignmentFieldsStateMutable = MutableStateFlow(DriverAssignmentFieldsState())
    val driverAssignmentFieldsState = driverAssignmentFieldsStateMutable.asStateFlow()

    fun loadDriverAssignments() {
        driverAssignmentsStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::driverAssignments) {
            driverAssignmentsStateMutable.value = it.toUiState()
        }
    }

    override fun reduce(event: DriverAssignmentEvent) {
        val action = object : DriverAssignmentAction {

            override fun changeDriversStats(stats: DriverStats) {
                driverAssignmentFieldsStateMutable.update { state ->
                    state.copy(selectedDriverStats = stats)
                }
            }

            override fun assignRequest(requestId: Int, driverId: Int, isReassign: Boolean) {
                assignRequestStateMutable.value = FetchResultUiState.Loading
                handle(
                    background = {
                        if (isReassign) {
                            repository.reassignDriverToRequest(requestId, driverId)
                        } else {
                            repository.assignDriverToRequest(requestId, driverId)
                        }
                    }
                ) {
                    assignRequestStateMutable.value = it.toUiState()
                }
            }

            override fun resetStates() {
                driverAssignmentFieldsStateMutable.update { state ->
                    state.copy(selectedDriverStats = null)
                }
                assignRequestStateMutable.value = FetchResultUiState.Idle
            }
        }

        event.apply(action)
    }
}