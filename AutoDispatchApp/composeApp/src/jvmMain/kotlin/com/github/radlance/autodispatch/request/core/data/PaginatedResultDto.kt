package com.github.radlance.autodispatch.request.core.data

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResultDto<T>(
    val items: List<T>,
    val totalCount: Long
)