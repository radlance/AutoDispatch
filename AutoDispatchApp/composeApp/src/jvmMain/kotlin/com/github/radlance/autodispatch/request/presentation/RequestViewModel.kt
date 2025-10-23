package com.github.radlance.autodispatch.request.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.domain.RequestRepository
import com.github.radlance.autodispatch.request.domain.RequestResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

class RequestViewModel(
    private val requestRepository: RequestRepository
) : BaseViewModel() {

    private val loadRequestUiStateMutable =
        MutableStateFlow<FetchResultUiState<RequestResponse, String>>(FetchResultUiState.Idle)

    val loadRequestUiState = loadRequestUiStateMutable.onStart {
        loadRequests()
    }.stateInViewModel(initialValue = loadRequestUiStateMutable.value)

    fun loadRequests() {
        loadRequestUiStateMutable.value = FetchResultUiState.Loading
        handle(background = requestRepository::requests) { result ->
            loadRequestUiStateMutable.value = result.toUiState()
        }
    }
}