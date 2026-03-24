package com.github.radlance.autodispatch.statistics.data

import com.github.radlance.autodispatch.common.data.ApiServiceJvm
import com.github.radlance.autodispatch.common.data.HandleRequest
import com.github.radlance.autodispatch.common.data.toDto
import com.github.radlance.autodispatch.common.domain.FetchResult
import com.github.radlance.autodispatch.statistics.domain.ReportRepository
import com.github.radlance.autodispatch.statistics.presentation.FileFormat
import com.github.radlance.autodispatch.statistics.presentation.ReportPeriod
import com.github.radlance.autodispatch.statistics.presentation.ReportType
import java.nio.file.Files
import java.nio.file.Paths

class RemoteReportRepository(
    private val apiService: ApiServiceJvm,
    private val handleRequest: HandleRequest
) : ReportRepository {

    override suspend fun downloadReport(
        reportType: ReportType,
        format: FileFormat,
        period: ReportPeriod,
        targetPath: String
    ): FetchResult<String, String> = handleRequest.handle {
        val bytes = apiService.downloadReport(
            DownloadReportRequestDto(
                reportType = reportType.toDto(),
                format = format.toDto(),
                period = period.toDto()
            )
        )

        val finalPath = ensureExtension(targetPath, format.extension)
        val path = Paths.get(finalPath)
        path.parent?.let { Files.createDirectories(it) }
        Files.write(path, bytes)
        finalPath
    }

    private fun ensureExtension(path: String, extension: String): String {
        val normalized = if (extension.startsWith(".")) extension else ".$extension"
        return if (path.lowercase().endsWith(normalized.lowercase())) path else "$path$normalized"
    }
}
