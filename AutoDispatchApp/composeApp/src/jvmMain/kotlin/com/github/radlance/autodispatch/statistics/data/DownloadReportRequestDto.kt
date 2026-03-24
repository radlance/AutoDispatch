package com.github.radlance.autodispatch.statistics.data

import kotlinx.serialization.Serializable

@Serializable
data class DownloadReportRequestDto(
    val reportType: ReportTypeDto,
    val format: FileFormatDto,
    val period: ReportPeriodDto
)

@Serializable
enum class ReportTypeDto {
    ALL,
    REQUESTS,
    DRIVERS,
    VEHICLES
}

@Serializable
enum class FileFormatDto {
    EXCEL,
    PDF,
    CSV
}

@Serializable
enum class ReportPeriodDto {
    TODAY,
    LAST_7_DAYS,
    LAST_30_DAYS
}
