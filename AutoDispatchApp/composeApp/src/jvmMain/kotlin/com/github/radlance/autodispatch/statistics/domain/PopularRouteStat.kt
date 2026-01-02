package com.github.radlance.autodispatch.statistics.domain

data class PopularRouteStat(
    val originCity: String,
    val destinationCity: String,
    val requestCount: Long
)
