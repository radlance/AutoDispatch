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

enum class FileFormat(val displayName: String, val extension: String) {
    EXCEL("Excel (.xlsx)", "xlsx"),
    PDF("PDF (.pdf)", "pdf"),
    CSV("CSV (.csv)", "csv")
}

enum class ReportPeriod(val displayName: String) {
    TODAY("Сегодня"),
    LAST_7_DAYS("Последние 7 дней"),
    LAST_30_DAYS("Последние 30 дней")
}
