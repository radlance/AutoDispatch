package com.github.radlance.autodispatch.request.domain

data class PaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)