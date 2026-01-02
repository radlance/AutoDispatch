package com.github.radlance.autodispatch.statistics.domain

data class GeneralStats(
    val totalRequests: Long,
    val completedRequests: Long,
    val totalVehicles: Long,
    val totalDrivers: Long
)