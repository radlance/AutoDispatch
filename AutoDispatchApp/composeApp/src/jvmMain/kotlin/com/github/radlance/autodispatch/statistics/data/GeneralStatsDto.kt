package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class GeneralStatsDto(
    val totalRequests: Long,
    val completedRequests: Long,
    val totalVehicles: Long,
    val totalDrivers: Long
)