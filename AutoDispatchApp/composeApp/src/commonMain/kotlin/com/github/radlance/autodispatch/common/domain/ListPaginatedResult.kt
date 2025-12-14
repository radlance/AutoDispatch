package com.github.radlance.autodispatch.common.domain

data class ListPaginatedResult<T>(
    val items: List<T>,
    val hasMore: Boolean
)