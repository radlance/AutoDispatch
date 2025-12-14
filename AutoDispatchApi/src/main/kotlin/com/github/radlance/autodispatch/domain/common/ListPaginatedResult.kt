package com.github.radlance.autodispatch.domain.common

import kotlinx.serialization.Serializable

@Serializable
data class ListPaginatedResult<T>(
    val items: List<T>,
    val hasMore: Boolean
)