package com.github.radlance.autodispatch.admin.core.data

import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserManagementFilters
import com.github.radlance.autodispatch.admin.core.domain.UserManagementRepository
import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toPaginatedResultUserDetailed
import com.github.radlance.autodispatch.common.data.toUserManagementFilters
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.common.domain.TablePaginatedResult

class RemoteUserManagementRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : UserManagementRepository {

    override suspend fun filters(): FetchResult<UserManagementFilters, String> =
        handleRequest.handle {
            apiService.userManagementFilters().toUserManagementFilters()
        }

    override suspend fun users(
        page: Int,
        pageSize: Int,
        searchQuery: String?,
        statusIds: List<Int>,
        roleIds: List<Int>
    ): FetchResult<TablePaginatedResult<UserDetailed>, String> = handleRequest.handle {
        apiService.usersDetailed(
            page = page,
            pageSize = pageSize,
            searchQuery = searchQuery,
            statusIds = statusIds,
            roleIds = roleIds
        ).toPaginatedResultUserDetailed()
    }
}