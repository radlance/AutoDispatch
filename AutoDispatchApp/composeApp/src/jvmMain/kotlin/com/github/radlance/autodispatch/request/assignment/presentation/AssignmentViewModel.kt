package com.github.radlance.autodispatch.request.assignment.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.assignment.domain.AssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AssignmentViewModel(
    private val repository: AssignmentRepository
) : BaseViewModel(), EventViewModel<AssignmentEvent> {

    private val requestAssignmentStateMutable =
        MutableStateFlow<FetchResultUiState<List<DriverStats>, String>>(FetchResultUiState.Idle)
    val requestAssignmentState = requestAssignmentStateMutable.asStateFlow()

    private val assignRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, String>>(FetchResultUiState.Idle)
    val assignRequestState = assignRequestStateMutable.asStateFlow()

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

            override fun assignRequest(requestId: Int, driverId: Int, isReassign: Boolean) {
                assignRequestStateMutable.value = FetchResultUiState.Loading
                handle(
                    background = {
                        if (isReassign) {
                            repository.reassignRequestToDriver(requestId, driverId)
                        } else {
                            repository.assignRequestToDriver(requestId, driverId)
                        }
                    }
                ) {
                    assignRequestStateMutable.value = it.toUiState()
                }
            }

            override fun resetStates() {
                assignmentFieldsStateMutable.update { state ->
                    state.copy(selectedDriverStats = null)
                }
                assignRequestStateMutable.value = FetchResultUiState.Idle
            }
        }

        event.apply(action)
    }
}