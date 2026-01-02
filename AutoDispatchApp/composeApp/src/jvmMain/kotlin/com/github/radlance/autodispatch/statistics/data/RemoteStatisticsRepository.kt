package com.github.radlance.autodispatch.statistics.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDashboardStatistics
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.statistics.domain.DashboardStatistics
import com.github.radlance.autodispatch.statistics.domain.StatisticsRepository

class RemoteStatisticsRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : StatisticsRepository {

    override suspend fun statistics(): FetchResult<DashboardStatistics, String> =
        handleRequest.handle {
            apiService.statistics().toDashboardStatistics()
        }
}