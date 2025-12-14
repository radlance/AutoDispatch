package com.github.radlance.autodispatch.request.core.domain

data class TablePaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)