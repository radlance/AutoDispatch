package com.github.radlance.autodispatch.driver.core.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.driver.core.domain.DriverRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class DriverViewModel(
    private val repository: DriverRepository
) : BaseViewModel() {
    private val driverScreenStateMutable = MutableStateFlow(DriverScreenState())
    val driverScreenState = driverScreenStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 300L

    init {
        triggerDriverLoad()
    }

    fun onDriverChanged() {
        if (driverScreenStateMutable.value.driversResultState is FetchResultUiState.Success) {
            triggerDriverLoad()
        }
    }

    fun triggerDriverLoad() {
        val state = driverScreenStateMutable.value
        val searchQuery = state.query.takeIf { it.isNotBlank() }

        val params = LastDriverRequestParams(
            page = state.pageIndex,
            pageSize = state.pageSize,
            searchQuery = searchQuery
        )
        driverScreenStateMutable.update { it.copy(lastAttemptedRequest = params) }

        loadDrivers(
            page = state.pageIndex + 1,
            pageSize = state.pageSize,
            searchQuery = searchQuery
        )
    }

    private fun loadDrivers(
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ) {
        driverScreenStateMutable.update { state ->
            state.copy(driversResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                repository.drivers(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery
                )
            }
        ) { drivers ->
            driverScreenStateMutable.update { state ->
                val uiState = drivers.toUiState()
                if (uiState is FetchResultUiState.Success) {
                    state.copy(
                        driversResultState = uiState,
                        lastSuccessfulRequest = uiState.data
                    )
                } else {
                    state.copy(driversResultState = uiState)
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        driverScreenStateMutable.update { it.copy(query = query, pageIndex = 0) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(debounceTime)
            triggerDriverLoad()
        }
    }

    fun retryLoadRequests() {
        val lastParams = driverScreenStateMutable.value.lastAttemptedRequest
        if (lastParams != null) {
            loadDrivers(
                page = lastParams.page + 1,
                pageSize = lastParams.pageSize,
                searchQuery = lastParams.searchQuery
            )
        } else {
            triggerDriverLoad()
        }
    }

    fun onPageIndexChanged(pageIndex: Int) {
        val safeIndex = max(0, pageIndex)
        driverScreenStateMutable.update {
            it.copy(pageIndex = safeIndex)
        }
        triggerDriverLoad()
    }

    fun onPageSizeChanged(newPageSize: Int) {
        val state = driverScreenStateMutable.value
        val oldPageSize = state.pageSize
        val oldPageIndex = state.pageIndex
        val absoluteOffset = oldPageIndex * oldPageSize
        val newPageIndex = absoluteOffset / newPageSize

        driverScreenStateMutable.update {
            it.copy(
                pageSize = newPageSize,
                pageIndex = newPageIndex
            )
        }

        triggerDriverLoad()
    }
}