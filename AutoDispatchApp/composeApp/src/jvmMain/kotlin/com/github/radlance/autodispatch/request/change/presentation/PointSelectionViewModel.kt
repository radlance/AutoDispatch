package com.github.radlance.autodispatch.request.change.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.change.domain.Coords
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.change.domain.PointSelectionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PointSelectionViewModel(
    private val repository: PointSelectionRepository
) : BaseViewModel() {
    private val fetchPointStateMutable = MutableStateFlow<FetchResultUiState<Coords, String>>(
        FetchResultUiState.Idle
    )
    val fetchPointState = fetchPointStateMutable.onStart {
        fetchCoords()
    }.stateInViewModel(initialValue = fetchPointStateMutable.value)

    private val pointsMutable = MutableStateFlow<List<PointDetailed>>(emptyList())
    val points = pointsMutable.asStateFlow()
    private var searchJob: Job? = null
    private val debounceTime = 600L

    fun fetchCoords() {
        fetchPointStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::fetchCoords) {
            fetchPointStateMutable.value = it.toUiState()
        }
    }

    fun searchPoint(query: String) {
        searchJob?.cancel()

        if (query.isEmpty()) {
            pointsMutable.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(debounceTime)
            handle(background = { repository.searchPoint(query) }) {
                pointsMutable.value = it
            }
        }
    }
}