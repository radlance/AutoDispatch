package com.github.radlance.autodispatch.statistics.domain

import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.statistics.presentation.FileFormat
import com.github.radlance.autodispatch.statistics.presentation.ReportPeriod
import com.github.radlance.autodispatch.statistics.presentation.ReportType

interface ReportRepository {

    suspend fun downloadReport(
        reportType: ReportType,
        format: FileFormat,
        period: ReportPeriod,
        targetPath: String
    ): FetchResult<String, String>
}
