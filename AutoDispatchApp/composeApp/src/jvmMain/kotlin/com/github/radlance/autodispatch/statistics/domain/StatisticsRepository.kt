package com.github.radlance.autodispatch.statistics.domain

import com.github.radlance.autodispatch.common.domain.FetchResult

interface StatisticsRepository {

    suspend fun statistics(): FetchResult<DashboardStatistics, String>
}