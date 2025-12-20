package com.github.radlance.autodispatch.driver.history.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.ListPaginatedResult
import com.github.radlance.autodispatch.driver.common.presentation.SearchPaginatedViewModel
import com.github.radlance.autodispatch.driver.history.domain.DriverHistory
import com.github.radlance.autodispatch.driver.history.domain.DriverHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class DriverHistoryViewModel(
    private val repository: DriverHistoryRepository
) : SearchPaginatedViewModel<DriverHistory>(pageSize = 3) {
    override suspend fun request(
        query: String?,
        page: Int,
        pageSize: Int
    ): FetchResult<ListPaginatedResult<DriverHistory>, String> = repository.history(
        driverId = state.value.selectedDriverId,
        searchQuery = query,
        page = page,
        pageSize = pageSize
    )

    fun loadNextItems(driverId: Int) {
        stateMutable.update {
            it.copy(selectedDriverId = driverId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
}