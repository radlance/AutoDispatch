package com.github.radlance.autodispatch.driver.request.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.driver.common.presentation.SearchPaginatedViewModel
import com.github.radlance.autodispatch.driver.request.domain.DriverRequest
import com.github.radlance.autodispatch.driver.request.domain.DriverRequestRepository
import com.github.radlance.autodispatch.request.assignment.domain.DriverAssignmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DriverRequestAssignmentViewModel(
    private val driverRequestRepository: DriverRequestRepository,
    private val driverAssignmentRepository: DriverAssignmentRepository
) : SearchPaginatedViewModel<DriverRequest>(
    pageSize = 3
) {
    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverRequest>, String> =
        driverRequestRepository.availableRequests(
            searchQuery = query,
            page = page,
            pageSize = pageSize
        )

    private val assignRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, RequestError>>(FetchResultUiState.Idle)
    val assignRequestState = assignRequestStateMutable.asStateFlow()

    fun loadNextItems() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    fun assignRequest(requestId: Int, driverId: Int) {
        assignRequestStateMutable.value = FetchResultUiState.Loading
        handle(
            background = {
                driverAssignmentRepository.assignDriverToRequest(requestId, driverId)
            }
        ) {
            assignRequestStateMutable.value = it.toUiState()
        }
    }

    override fun resetState() {
        super.resetState()
        assignRequestStateMutable.value = FetchResultUiState.Idle
    }
}