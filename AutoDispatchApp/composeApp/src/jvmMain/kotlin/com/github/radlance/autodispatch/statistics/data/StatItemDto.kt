package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class StatItemDto(
    val label: String,
    val count: Long
)
