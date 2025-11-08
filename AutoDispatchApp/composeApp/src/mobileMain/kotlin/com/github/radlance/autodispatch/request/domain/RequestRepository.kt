package com.github.radlance.autodispatch.request.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.reuqest.core.domain.Request

interface RequestRepository {

    suspend fun request(): FetchResult<List<Request>, String>
}