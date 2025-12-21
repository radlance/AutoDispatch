package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.driver.common.presentation.SearchPaginatedViewModel
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverAssignmentViewModel(
    private val repository: DriverAssignmentRepository
) : SearchPaginatedViewModel<DriverStats>(
    pageSize = 5
) {

    private val assignRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, DeliveryError>>(FetchResultUiState.Idle)
    val assignRequestState = assignRequestStateMutable.asStateFlow()

    fun assignRequest(requestId: Int, driverId: Int, isReassign: Boolean) {
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

    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverStats>, String> {
        return repository.driverAssignments(
            page = page,
            pageSize = pageSize,
            searchQuery = query
        )
    }

    override fun resetState() {
        super.resetState()
        assignRequestStateMutable.value = FetchResultUiState.Idle
    }

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}