package com.github.radlance.autodispatch.common.domain

data class TablePaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)