package com.github.radlance.autodispatch.common.data

import kotlinx.serialization.Serializable

@Serializable
data class ListPaginatedResultDto<T>(
    val items: List<T>,
    val hasMore: Boolean
)