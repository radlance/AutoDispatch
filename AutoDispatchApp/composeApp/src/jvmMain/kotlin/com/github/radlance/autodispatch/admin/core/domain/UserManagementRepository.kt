package com.github.radlance.autodispatch.admin.core.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult

interface UserManagementRepository {

    suspend fun filters(): FetchResult<UserManagementFilters, String>

    suspend fun users(
        page: Int = 1,
        pageSize: Int = 10,
        searchQuery: String? = null,
        statusIds: List<Int> = emptyList(),
        roleIds: List<Int> = emptyList()
    ): FetchResult<TablePaginatedResult<UserDetailed>, String>
}