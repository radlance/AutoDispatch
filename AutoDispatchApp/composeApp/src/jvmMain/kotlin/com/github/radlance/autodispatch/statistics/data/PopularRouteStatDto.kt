package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class PopularRouteStatDto(
    val originCity: String,
    val destinationCity: String,
    val requestCount: Long
)
