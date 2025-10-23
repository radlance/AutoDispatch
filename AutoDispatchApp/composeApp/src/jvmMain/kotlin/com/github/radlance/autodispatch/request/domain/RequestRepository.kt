package com.github.radlance.autodispatch.request.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface RequestRepository {

    suspend fun requests(): FetchResult<RequestResponse, String>
}