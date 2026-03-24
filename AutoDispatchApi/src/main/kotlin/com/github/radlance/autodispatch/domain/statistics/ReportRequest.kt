package com.github.radlance.autodispatch.domain.statistics

import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class ReportRequest(
    val reportType: ReportType,
    val format: ReportFormat,
    val period: ReportPeriod
)

@Serializable
enum class ReportType {
    ALL,
    REQUESTS,
    DRIVERS,
    VEHICLES
}

@Serializable
enum class ReportFormat {
    EXCEL,
    PDF,
    CSV
}

@Serializable
enum class ReportPeriod {
    TODAY,
    LAST_7_DAYS,
    LAST_30_DAYS
}

data class ReportDateRange(
    val start: OffsetDateTime,
    val end: OffsetDateTime
)

fun ReportPeriod.toRange(now: OffsetDateTime = OffsetDateTime.now()): ReportDateRange {
    val zoneId = ZoneId.systemDefault()
    val end = now
    val start = when (this) {
        ReportPeriod.TODAY -> now.toLocalDate().atStartOfDay(zoneId).toOffsetDateTime()
        ReportPeriod.LAST_7_DAYS -> now.minusDays(7).toLocalDate().atStartOfDay(zoneId).toOffsetDateTime()
        ReportPeriod.LAST_30_DAYS -> now.minusDays(30).toLocalDate().atStartOfDay(zoneId).toOffsetDateTime()
    }
    return ReportDateRange(start = start, end = end)
}
