package com.github.radlance.autodispatch.request.change.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.request.change.domain.Coords
import com.github.radlance.autodispatch.request.change.domain.PointDetailed
import com.github.radlance.autodispatch.request.change.domain.PointSelectionRepository
import com.github.radlance.autodispatch.request.change.domain.PointValidationError
import com.github.radlance.autodispatch.request.change.domain.ValidatedPoint
import com.github.radlance.autodispatch.request.core.domain.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PointSelectionViewModel(
    private val repository: PointSelectionRepository
) : BaseViewModel() {
    private val fetchPointStateMutable = MutableStateFlow<FetchResultUiState<Coords, String>>(
        FetchResultUiState.Idle
    )
    val fetchPointState = fetchPointStateMutable.asStateFlow()

    private val validationStateMutable =
        MutableStateFlow<FetchResultUiState<ValidatedPoint, PointValidationError>>(
            FetchResultUiState.Idle
        )

    val validationState =
        validationStateMutable.asStateFlow()



    private val pointsMutable = MutableStateFlow<List<PointDetailed>>(emptyList())
    val points = pointsMutable.asStateFlow()
    private var searchJob: Job? = null
    private val debounceTime = 300L

    fun fetchCoords(cityName: String) {
        fetchPointStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.cityCenter(cityName) }) {
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

    fun confirmPointSelection(
        point: Point,
        selectedCityName: String
    ) {
        validationStateMutable.value = FetchResultUiState.Loading
        handle(background = { repository.validatePointInCity(point, selectedCityName) }) {
            validationStateMutable.value = it.toUiState()
        }
    }

    fun resetValidationState() {
        validationStateMutable.value = FetchResultUiState.Idle
    }
}