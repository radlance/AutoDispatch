package com.github.radlance.autodispatch.route

import com.github.radlance.autodispatch.domain.statistics.ReportFormat
import com.github.radlance.autodispatch.domain.statistics.ReportRequest
import com.github.radlance.autodispatch.repository.StatisticsRepository
import com.github.radlance.autodispatch.service.ReportService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.statistics(repository: StatisticsRepository, reportService: ReportService) {
    authenticate {
        route("/statistics") {
            get {
                val statistics = repository.getStatistics()
                call.respond(HttpStatusCode.OK, statistics)
            }

            post("/report") {
                val request = call.receive<ReportRequest>()
                val report = reportService.generate(request)
                val contentType = when (request.format) {
                    ReportFormat.EXCEL -> ContentType.parse(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )

                    ReportFormat.PDF -> ContentType.Application.Pdf
                    ReportFormat.CSV -> ContentType.Text.CSV
                }
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment
                        .withParameter(ContentDisposition.Parameters.FileName, report.fileName)
                        .toString()
                )
                call.respondBytes(report.bytes, contentType)
            }
        }
    }
}
