package com.github.radlance.autodispatch.domain.statistics

import kotlinx.serialization.Serializable

@Serializable
data class GeneralStats(
    val totalRequests: Long,
    val completedRequests: Long,
    val totalVehicles: Long,
    val totalDrivers: Long
)