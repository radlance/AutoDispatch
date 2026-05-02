package com.github.radlance.autodispatch.admin.change.data

import com.github.radlance.autodispatch.admin.change.domain.ChangeUserRepository
import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.domain.FetchResult

class RemoteChangeUserRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : ChangeUserRepository {

    override suspend fun blockUser(userId: Int): FetchResult<Unit, String> = handleRequest.handle {
        apiService.blockUser(userId)
    }

    override suspend fun unblockUser(userId: Int): FetchResult<Unit, String> = handleRequest.handle {
        apiService.unblockUser(userId)
    }
}