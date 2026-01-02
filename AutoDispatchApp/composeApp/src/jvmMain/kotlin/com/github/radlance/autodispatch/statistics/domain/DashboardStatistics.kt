package com.github.radlance.autodispatch.statistics.domain

data class DashboardStatistics(
    val general: GeneralStats,
    val requestsByStatus: List<StatItem>,
    val requestsByCargoType: List<StatItem>,
    val driversByStatus: List<StatItem>,
    val vehiclesByStatus: List<StatItem>,
    val topDrivers: List<TopDriverStat>,
    val popularRoutes: List<PopularRouteStat>
)
