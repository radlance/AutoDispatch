package com.github.radlance.autodispatch.admin.change.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface ChangeUserRepository {

    suspend fun blockUser(userId: Int): FetchResult<Unit, String>

    suspend fun unblockUser(userId: Int): FetchResult<Unit, String>
}