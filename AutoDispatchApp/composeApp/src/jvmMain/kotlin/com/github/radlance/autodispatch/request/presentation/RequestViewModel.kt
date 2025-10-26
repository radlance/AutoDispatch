package com.github.radlance.autodispatch.request.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.domain.RequestRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

class RequestViewModel(
    private val requestRepository: RequestRepository
) : BaseViewModel() {

    private val requestScreenStateMutable = MutableStateFlow(RequestScreenState())

    val requestScreenState = requestScreenStateMutable.onStart {
        loadAllInformation()
    }.stateInViewModel(initialValue = requestScreenStateMutable.value)

    fun loadAllInformation() {
        requestScreenStateMutable.update { state ->
            state.copy(filters = FetchResultUiState.Loading)
        }
        handle(
            background = {
                coroutineScope {
                    val filters = async {
                        requestRepository.filters()
                    }

                    val requests = async {
                        requestRepository.requests()
                    }

                    filters.await() to requests.await()
                }
            }
        ) { (filters, requests) ->
            requestScreenStateMutable.update { state ->
                state.copy(
                    filters = filters.toUiState(),
                    requestsResultState = requests.toUiState()
                )
            }
        }
    }

    fun loadRequests(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null,
        originCityIds: List<Int> = emptyList(),
        destinationCityIds: List<Int> = emptyList(),
        cargoTypeIds: List<Int> = emptyList(),
        statusIds: List<Int> = emptyList(),
        driverIds: List<Int> = emptyList(),
        vehicleIds: List<Int> = emptyList()
    ) {
        requestScreenStateMutable.update { state ->
            state.copy(requestsResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                requestRepository.requests(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery,
                    originCityIds = originCityIds,
                    destinationCityIds = destinationCityIds,
                    cargoTypeIds = cargoTypeIds,
                    statusIds = statusIds,
                    driverIds = driverIds,
                    vehicleIds = vehicleIds
                )
            }
        ) { requests ->
            requestScreenStateMutable.update { state ->
                state.copy(
                    requestsResultState = requests.toUiState()
                )
            }
        }
    }
}