package com.github.radlance.autodispatch.vehicle.core.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class VehicleViewModel(
    private val repository: VehicleRepository
) : BaseViewModel() {
    private val vehicleScreenStateMutable = MutableStateFlow(VehicleScreenState())
    val vehicleScreenState = vehicleScreenStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 500L

    init {
        triggerVehicleLoad()
    }

    fun onVehicleChanged() {
        if (vehicleScreenStateMutable.value.vehicleResultState is FetchResultUiState.Success) {
            triggerVehicleLoad()
        }
    }

    fun triggerVehicleLoad() {
        val state = vehicleScreenStateMutable.value
        val searchQuery = state.query.takeIf { it.isNotBlank() }

        val params = LastVehicleRequestParams(
            page = state.pageIndex,
            pageSize = state.pageSize,
            searchQuery = searchQuery
        )

        vehicleScreenStateMutable.update { it.copy(lastAttemptedRequest = params) }

        loadVehicles(
            page = state.pageIndex + 1,
            pageSize = state.pageSize,
            searchQuery = searchQuery
        )
    }

    private fun loadVehicles(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ) {
        vehicleScreenStateMutable.update { state ->
            state.copy(vehicleResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                repository.vehicles(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )
            }
        ) { vehicles ->
            vehicleScreenStateMutable.update { state ->
                val uiState = vehicles.toUiState()
                if (uiState is FetchResultUiState.Success) {
                    state.copy(
                        vehicleResultState = uiState,
                        lastSuccessfulRequest = uiState.data
                    )
                } else {
                    state.copy(vehicleResultState = uiState)
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        vehicleScreenStateMutable.update { it.copy(query = query, pageIndex = 0) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(debounceTime)
            triggerVehicleLoad()
        }
    }

    fun retryLoadRequests() {
        val lastParams = vehicleScreenStateMutable.value.lastAttemptedRequest
        if (lastParams != null) {
            loadVehicles(
                page = lastParams.page + 1,
                pageSize = lastParams.pageSize,
                searchQuery = lastParams.searchQuery
            )
        } else {
            triggerVehicleLoad()
        }
    }

    fun onPageIndexChanged(pageIndex: Int) {
        val safeIndex = max(0, pageIndex)
        vehicleScreenStateMutable.update {
            it.copy(pageIndex = safeIndex)
        }
        triggerVehicleLoad()
    }

    fun onPageSizeChanged(newPageSize: Int) {
        val state = vehicleScreenStateMutable.value
        val oldPageSize = state.pageSize
        val oldPageIndex = state.pageIndex
        val absoluteOffset = oldPageIndex * oldPageSize
        val newPageIndex = absoluteOffset / newPageSize

        vehicleScreenStateMutable.update {
            it.copy(
                pageSize = newPageSize,
                pageIndex = newPageIndex
            )
        }

        triggerVehicleLoad()
    }
}