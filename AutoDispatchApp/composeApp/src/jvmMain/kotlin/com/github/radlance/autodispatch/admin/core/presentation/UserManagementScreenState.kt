package com.github.radlance.autodispatch.admin.core.presentation

import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserManagementFilters
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState

data class UserManagementScreenState(
    val filters: FetchResultUiState<UserManagementFilters, String> = FetchResultUiState.Loading,
    val usersResultState: FetchResultUiState<TablePaginatedResult<UserDetailed>, String> = FetchResultUiState.Loading,

    val query: String = "",
    val selectedStatuses: List<String> = emptyList(),
    val selectedRoles: List<String> = emptyList(),

    val pageIndex: Int = 0,
    val pageSize: Int = 15,

    val lastSuccessfulRequest: TablePaginatedResult<UserDetailed>? = null,
    val lastAttemptedRequest: LastUsersRequestParams? = null
)

data class LastUsersRequestParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String?,
    val statusIds: List<Int>,
    val roleIds: List<Int>
)