package com.github.radlance.autodispatch.domain.statistics

import kotlinx.serialization.Serializable

@Serializable
data class PopularRouteStat(
    val originCity: String,
    val destinationCity: String,
    val requestCount: Long
)
