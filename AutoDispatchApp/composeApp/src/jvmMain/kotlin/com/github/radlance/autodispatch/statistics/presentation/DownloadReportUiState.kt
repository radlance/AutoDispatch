package com.github.radlance.autodispatch.statistics.presentation

data class DownloadReportUiState(
    val reportType: ReportType = ReportType.ALL,
    val fileFormat: FileFormat = FileFormat.EXCEL,
    val reportPeriod: ReportPeriod = ReportPeriod.TODAY
)

enum class ReportType(val displayName: String) {
    ALL("Полный отчёт"),
    REQUESTS("Заявки"),
    DRIVERS("Водители"),
    VEHICLES("Автомобили")
}

enum class FileFormat(val displayName: String) {
    EXCEL("Excel (.xlsx)"),
    PDF("PDF (.pdf)"),
    CSV("CSV (.csv)")
}

enum class ReportPeriod(val displayName: String) {
    TODAY("Сегодня"),
    LAST_7_DAYS("Последнии 7 дней"),
    LAST_30_DAYS("Последнии 30 дней")
}
