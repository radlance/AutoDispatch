package com.github.radlance.autodispatch.statistics.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.statistics.domain.DashboardStatistics
import com.github.radlance.autodispatch.statistics.domain.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StatisticsViewModel(private val repository: StatisticsRepository) : BaseViewModel() {

    private val statisticsStateMutable =
        MutableStateFlow<FetchResultUiState<DashboardStatistics, String>>(
            FetchResultUiState.Idle
        )
    val statisticsState = statisticsStateMutable.asStateFlow()

    fun loadStatistics() {
        statisticsStateMutable.value = FetchResultUiState.Loading
        handle(background = repository::statistics) {
            statisticsStateMutable.value = it.toUiState()
        }
    }
}