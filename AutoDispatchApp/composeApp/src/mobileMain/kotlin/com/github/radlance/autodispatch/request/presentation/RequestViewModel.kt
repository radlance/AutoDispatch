package com.github.radlance.autodispatch.request.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class RequestViewModel(
    private val requestRepository: RequestRepository
) : BaseViewModel() {

    private val requestsStateMutable =
        MutableStateFlow<FetchResultUiState<List<Request>, String>>(FetchResultUiState.Idle)
    var requestsState = requestsStateMutable.onStart {
        fetchRequests()
    }.stateInViewModel(initialValue = requestsStateMutable.value)

    fun fetchRequests() {
        requestsStateMutable.value = FetchResultUiState.Loading
        handle(background = requestRepository::request) {
            requestsStateMutable.value = it.toUiState()
        }
    }
}