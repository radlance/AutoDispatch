package com.github.radlance.autodispatch.admin.core.presentation

import androidx.lifecycle.viewModelScope
import com.github.radlance.autodispatch.admin.core.domain.UserManagementRepository
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.applyFilterSelection
import com.github.radlance.autodispatch.common.presentation.toUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class UserManagementViewModel(
    private val repository: UserManagementRepository
) : BaseViewModel() {

    private val userManagementScreenStateMutable = MutableStateFlow(UserManagementScreenState())
    val userManagementScreenState = userManagementScreenStateMutable.asStateFlow()

    private var searchJob: Job? = null
    private val debounceTime = 300L

    init {
        loadFilters()
    }

    fun loadFilters() {
        userManagementScreenStateMutable.update { it.copy(filters = FetchResultUiState.Loading) }
        handle(
            background = { repository.filters() }
        ) { filtersResult ->
            userManagementScreenStateMutable.update { state ->
                state.copy(filters = filtersResult.toUiState())
            }
            if (filtersResult is FetchResult.Success) {
                triggerRequestLoad()
            }
        }
    }

    fun triggerRequestLoad() {
        val state = userManagementScreenStateMutable.value
        val filters = (state.filters as? FetchResultUiState.Success)?.data ?: return
        val searchQuery = state.query.takeIf { it.isNotBlank() }

        val statusIds = applyFilterSelection(
            selectedNames = state.selectedStatuses,
            allItems = filters.statuses,
            nameSelector = { it.title },
            idSelector = { it.id }
        )

        val roleIds = applyFilterSelection(
            selectedNames = state.selectedRoles,
            allItems = filters.roles,
            nameSelector = { it.title },
            idSelector = { it.id }
        )

        val params =
            LastUsersRequestParams(
                page = state.pageIndex,
                pageSize = state.pageSize,
                searchQuery = searchQuery,
                statusIds = statusIds,
                roleIds = roleIds
            )
        userManagementScreenStateMutable.update { it.copy(lastAttemptedRequest = params) }

        loadUsers(
            page = state.pageIndex + 1,
            pageSize = state.pageSize,
            searchQuery = searchQuery,
            statusIds = statusIds,
            roleIds = roleIds
        )
    }

    private fun loadUsers(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        statusIds: List<Int>,
        roleIds: List<Int>
    ) {
        userManagementScreenStateMutable.update { state ->
            state.copy(usersResultState = FetchResultUiState.Loading)
        }
        handle(
            background = {
                repository.users(
                    page = page,
                    pageSize = pageSize,
                    searchQuery = searchQuery,
                    statusIds = statusIds,
                    roleIds = roleIds
                )
            }
        ) { requests ->
            userManagementScreenStateMutable.update { state ->
                val uiState = requests.toUiState()
                if (uiState is FetchResultUiState.Success) {
                    state.copy(
                        usersResultState = uiState,
                        lastSuccessfulRequest = uiState.data
                    )
                } else {
                    state.copy(usersResultState = uiState)
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        userManagementScreenStateMutable.update { it.copy(query = query, pageIndex = 0) }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(debounceTime)
            triggerRequestLoad()
        }
    }

    fun onRolesChanged(roles: List<String>) {
        userManagementScreenStateMutable.update {
            it.copy(
                selectedRoles = roles,
                pageIndex = 0
            )
        }
        triggerRequestLoad()
    }

    fun onStatusesChanged(statuses: List<String>) {
        userManagementScreenStateMutable.update {
            it.copy(
                selectedStatuses = statuses,
                pageIndex = 0
            )
        }
        triggerRequestLoad()
    }

    fun onPageIndexChanged(pageIndex: Int) {
        val safeIndex = max(0, pageIndex)
        userManagementScreenStateMutable.update {
            it.copy(pageIndex = safeIndex)
        }
        triggerRequestLoad()
    }

    fun onPageSizeChanged(newPageSize: Int) {
        val state = userManagementScreenStateMutable.value
        val oldPageSize = state.pageSize
        val oldPageIndex = state.pageIndex
        val absoluteOffset = oldPageIndex * oldPageSize
        val newPageIndex = absoluteOffset / newPageSize

        userManagementScreenStateMutable.update {
            it.copy(
                pageSize = newPageSize,
                pageIndex = newPageIndex
            )
        }

        triggerRequestLoad()
    }

    fun retryLoadRequests() {
        val lastParams = userManagementScreenStateMutable.value.lastAttemptedRequest
        if (lastParams != null) {
            loadUsers(
                page = lastParams.page + 1,
                pageSize = lastParams.pageSize,
                searchQuery = lastParams.searchQuery,
                statusIds = lastParams.statusIds,
                roleIds = lastParams.roleIds
            )
        } else {
            triggerRequestLoad()
        }
    }

    fun onUsersChanged() {
        if (userManagementScreenStateMutable.value.usersResultState is FetchResultUiState.Success) {
            triggerRequestLoad()
        }
    }
}