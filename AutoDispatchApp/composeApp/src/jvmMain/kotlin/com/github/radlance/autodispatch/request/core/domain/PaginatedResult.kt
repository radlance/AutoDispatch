package com.github.radlance.autodispatch.request.core.domain

data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)