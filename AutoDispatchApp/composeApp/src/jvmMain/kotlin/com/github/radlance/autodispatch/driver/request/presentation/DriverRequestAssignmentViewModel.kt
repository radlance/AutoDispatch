package com.github.radlance.autodispatch.driver.request.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.delivery.domain.DeliveryError
import com.github.radlance.autodispatch.driver.common.presentation.DriverPaginatedViewModel
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
) : DriverPaginatedViewModel<DriverRequest, ListPaginatedResult<DriverRequest>>() {
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

    override fun getItems(result: ListPaginatedResult<DriverRequest>): List<DriverRequest> {
        return result.items
    }

    override fun hasMore(result: ListPaginatedResult<DriverRequest>): Boolean {
        return result.hasMore
    }

    private val assignRequestStateMutable =
        MutableStateFlow<FetchResultUiState<Unit, DeliveryError>>(FetchResultUiState.Idle)
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