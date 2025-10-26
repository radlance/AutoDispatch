package com.github.radlance.autodispatch.request.data

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResultDto<T>(
    val items: List<T>,
    val totalCount: Long
)