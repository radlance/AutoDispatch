package com.github.radlance.autodispatch.driver.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.driver.domain.DriverRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DriverViewModel(
    private val repository: DriverRepository
) : BaseViewModel() {
    private val driverScreenStateMutable = MutableStateFlow(DriverScreenState())
    val driverScreenState = driverScreenStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 500L

    init {
        triggerDriverLoad()
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

        loadRequests(
            page = state.pageIndex + 1,
            pageSize = state.pageSize,
            searchQuery = searchQuery
        )
    }

    private fun loadRequests(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
    ) {
        driverScreenStateMutable.update { state ->
            state.copy(driversResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                repository.requests(
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
                        lastSuccessfulRequests = uiState.data
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
            loadRequests(
                page = lastParams.page + 1,
                pageSize = lastParams.pageSize,
                searchQuery = lastParams.searchQuery
            )
        } else {
            triggerDriverLoad()
        }
    }
}