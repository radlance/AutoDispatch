package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatisticsDto(
    val general: GeneralStatsDto,
    val requestsByStatus: List<StatItemDto>,
    val requestsByCargoType: List<StatItemDto>,
    val driversByStatus: List<StatItemDto>,
    val vehiclesByStatus: List<StatItemDto>,
    val topDrivers: List<TopDriverStatDto>,
    val popularRoutes: List<PopularRouteStatDto>
)
