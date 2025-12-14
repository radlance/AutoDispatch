package com.github.radlance.autodispatch.domain.common

import kotlinx.serialization.Serializable

@Serializable
data class TablePaginatedResult<T>(
    val items: List<T>,
    val totalCount: Long
)