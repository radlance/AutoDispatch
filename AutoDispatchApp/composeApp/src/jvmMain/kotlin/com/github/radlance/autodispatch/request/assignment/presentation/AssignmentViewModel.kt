package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.assignment.domain.AssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.assignment.domain.RequestAssignment
import com.github.radlance.autodispatch.request.assignment.domain.VehicleStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class AssignmentViewModel(
    private val repository: AssignmentRepository
) : BaseViewModel(), EventViewModel<AssignmentEvent> {

    private val requestAssignmentStateMutable =
        MutableStateFlow<FetchResultUiState<RequestAssignment, String>>(FetchResultUiState.Idle)
    val requestAssignmentState = requestAssignmentStateMutable.onStart {
        loadRequestAssignment()
    }.stateInViewModel(initialValue = requestAssignmentStateMutable.value)

    private val assignmentFieldsStateMutable = MutableStateFlow(AssignmentFieldsState())
    val assignmentFieldsState = assignmentFieldsStateMutable.asStateFlow()

    fun loadRequestAssignment() {
        requestAssignmentStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::requestAssignment) {
            requestAssignmentStateMutable.value = it.toUiState()
        }
    }

    override fun reduce(event: AssignmentEvent) {
        val action = object : AssignmentAction {

            override fun changeDriversStats(stats: DriverStats) {
                assignmentFieldsStateMutable.update { state ->
                    state.copy(selectedDriverStats = stats)
                }
            }

            override fun changeVehicleStats(stats: VehicleStats) {
                assignmentFieldsStateMutable.update { state ->
                    state.copy(selectedVehicleStats = stats)
                }
            }

            override fun resetFieldsState() {
                assignmentFieldsStateMutable.update { state ->
                    state.copy(selectedDriverStats = null, selectedVehicleStats = null)
                }
            }
        }

        event.apply(action)
    }
}