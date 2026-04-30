package com.github.radlance.autodispatch.service

import com.github.radlance.autodispatch.domain.statistics.ReportDateRange
import com.github.radlance.autodispatch.domain.statistics.ReportFormat
import com.github.radlance.autodispatch.domain.statistics.ReportPeriod
import com.github.radlance.autodispatch.domain.statistics.ReportRequest
import com.github.radlance.autodispatch.domain.statistics.ReportType
import com.github.radlance.autodispatch.domain.statistics.toRange
import com.github.radlance.autodispatch.repository.DriverReportRow
import com.github.radlance.autodispatch.repository.ReportRepository
import com.github.radlance.autodispatch.repository.RequestReportRow
import com.github.radlance.autodispatch.repository.VehicleReportRow
import com.lowagie.text.Document
import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ReportFile(
    val bytes: ByteArray,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReportFile

        if (!bytes.contentEquals(other.bytes)) return false
        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        return result
    }
}

data class ReportSection(
    val title: String,
    val headers: List<String>,
    val rows: List<List<String>>
)

class ReportService(
    private val repository: ReportRepository
) {

    suspend fun generate(request: ReportRequest): ReportFile {
        val range = request.period.toRange()
        val sections = when (request.reportType) {
            ReportType.ALL -> listOf(
                buildRequestsSection(range),
                buildDriversSection(range),
                buildVehiclesSection(range)
            )

            ReportType.REQUESTS -> listOf(buildRequestsSection(range))
            ReportType.DRIVERS -> listOf(buildDriversSection(range))
            ReportType.VEHICLES -> listOf(buildVehiclesSection(range))
        }

        val hasAnyData = sections.any { it.rows.isNotEmpty() }
        if (!hasAnyData) {
            throw NoSuchElementException("Нет данных за выбранный период")
        }

        val title = "Отчёт за период: ${request.period.displayName()}"
        val bytes = when (request.format) {
            ReportFormat.CSV -> generateCsv(title, sections)
            ReportFormat.EXCEL -> generateExcel(title, sections)
            ReportFormat.PDF -> generatePdf(title, sections)
        }

        val fileName = buildFileName(request.reportType, request.period, request.format)
        return ReportFile(bytes = bytes, fileName = fileName)
    }

    private suspend fun buildRequestsSection(range: ReportDateRange): ReportSection {
        val rows = repository.requests(range).map { it.toRequestRow() }
        return ReportSection(
            title = "Заявки",
            headers = listOf(
                "Номер",
                "Статус",
                "Создана",
                "План. загрузка",
                "План. разгрузка",
                "Откуда",
                "Куда",
                "Тип груза",
                "Вес (кг)",
                "Объём (м³)",
                "Клиент",
                "Телефон клиента",
                "Водитель",
                "Автомобиль"
            ),
            rows = rows
        )
    }

    private suspend fun buildDriversSection(range: ReportDateRange): ReportSection {
        val rows = repository.drivers(range).map { it.toDriverRow() }
        return ReportSection(
            title = "Водители",
            headers = listOf(
                "ФИО",
                "Телефон",
                "Статус",
                "Автомобиль",
                "Назначено заявок"
            ),
            rows = rows
        )
    }

    private suspend fun buildVehiclesSection(range: ReportDateRange): ReportSection {
        val rows = repository.vehicles(range).map { it.toVehicleRow() }
        return ReportSection(
            title = "Автомобили",
            headers = listOf(
                "Модель",
                "Гос. номер",
                "Регион",
                "Г/п (кг)",
                "Текущий водитель",
                "Назначено заявок"
            ),
            rows = rows
        )
    }

    private fun generateCsv(title: String, sections: List<ReportSection>): ByteArray {
        val builder = StringBuilder()
        builder.appendLine(title)
        builder.appendLine()

        sections.forEachIndexed { index, section ->
            builder.appendLine(section.title)
            if (section.rows.isEmpty()) {
                builder.appendLine("Нет данных за выбранный период")
            } else {
                builder.appendLine(csvLine(section.headers))
                section.rows.forEach { builder.appendLine(csvLine(it)) }
            }
            if (index != sections.lastIndex) builder.appendLine()
        }

        return builder.toString().toByteArray(Charsets.UTF_8)
    }

    private fun generateExcel(title: String, sections: List<ReportSection>): ByteArray {
        val workbook = XSSFWorkbook()
        val titleFont = workbook.createFont().apply { bold = true; fontHeightInPoints = 14 }
        val headerFont = workbook.createFont().apply { bold = true }

        val titleStyle = workbook.createCellStyle().apply {
            setFont(titleFont)
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.CENTER
        }
        val headerStyle = workbook.createCellStyle().apply {
            setFont(headerFont)
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
        }

        sections.forEach { section ->
            val sheet = workbook.createSheet(section.title)
            var rowIndex = 0
            
            val maxColumns = section.headers.size.coerceAtLeast(1)
            sheet.addMergedRow(rowIndex, title, titleStyle, 0, maxColumns - 1)
            rowIndex++

            rowIndex = sheet.addMergedRow(rowIndex, section.title, headerStyle, 0, section.headers.size - 1)
            rowIndex++

            if (section.rows.isEmpty()) {
                rowIndex = sheet.addMergedRow(
                    rowIndex,
                    "Нет данных за выбранный период",
                    null,
                    0,
                    section.headers.size - 1
                )
            } else {
                val headerRow = sheet.createRow(rowIndex++)
                section.headers.forEachIndexed { i, header ->
                    headerRow.createCell(i).apply {
                        setCellValue(header)
                        cellStyle = headerStyle
                    }
                }

                section.rows.forEach { row ->
                    val sheetRow = sheet.createRow(rowIndex++)
                    row.forEachIndexed { i, value ->
                        sheetRow.createCell(i).setCellValue(value)
                    }
                }
            }

            repeat(maxColumns) { col ->
                sheet.autoSizeColumn(col)
            }
        }

        val out = ByteArrayOutputStream()
        workbook.use { it.write(out) }
        return out.toByteArray()
    }

    private fun generatePdf(title: String, sections: List<ReportSection>): ByteArray {
        val out = ByteArrayOutputStream()
        val document = Document(PageSize.A4.rotate(), 36f, 36f, 36f, 36f)
        PdfWriter.getInstance(document, out)
        document.open()

        val titleFont = Font(Font.HELVETICA, 16f, Font.BOLD)
        val sectionFont = Font(Font.HELVETICA, 13f, Font.BOLD)
        val headerFont = Font(Font.HELVETICA, 10f, Font.BOLD)
        val cellFont = Font(Font.HELVETICA, 10f, Font.NORMAL)

        sections.forEachIndexed { index, section ->
            if (index > 0) {
                document.newPage()
            }
            
            document.add(Paragraph(title, titleFont))
            document.add(Paragraph(" "))
            document.add(Paragraph(section.title, sectionFont))
            document.add(Paragraph(" "))

            if (section.rows.isEmpty()) {
                document.add(Paragraph("Нет данных за выбранный период", cellFont))
            } else {
                val table = PdfPTable(section.headers.size).apply { widthPercentage = 100f }
                section.headers.forEach { header ->
                    table.addCell(PdfPCell(Paragraph(header, headerFont)).apply {
                        horizontalAlignment = Element.ALIGN_CENTER
                    })
                }
                section.rows.forEach { row ->
                    row.forEach { value ->
                        table.addCell(PdfPCell(Paragraph(value, cellFont)))
                    }
                }
                document.add(table)
            }
        }

        document.close()
        return out.toByteArray()
    }

    private fun RequestReportRow.toRequestRow(): List<String> {
        return listOf(
            requestNumber.orEmpty(),
            status,
            createdAt.formatOrEmpty(),
            plannedLoadingAt.formatOrEmpty(),
            plannedUnloadingAt.formatOrEmpty(),
            originCity.orEmpty(),
            destinationCity.orEmpty(),
            cargoType.orEmpty(),
            cargoWeight?.toString().orEmpty(),
            cargoVolume?.toString().orEmpty(),
            customerName.orEmpty(),
            customerPhone.orEmpty(),
            driverName.orEmpty(),
            formatVehicle(vehicleModel, vehicleLicensePlate, vehicleRegionCode)
        )
    }

    private fun DriverReportRow.toDriverRow(): List<String> {
        return listOf(
            fullName,
            phoneNumber.orEmpty(),
            status,
            formatVehicle(vehicleModel, vehicleLicensePlate, vehicleRegionCode, vehiclePayloadCapacity),
            assignedRequests.toString()
        )
    }

    private fun VehicleReportRow.toVehicleRow(): List<String> {
        return listOf(
            model,
            licensePlate,
            regionCode,
            payloadCapacity.toString(),
            currentDriverName.orEmpty(),
            assignedRequests.toString()
        )
    }

    private fun OffsetDateTime?.formatOrEmpty(): String {
        if (this == null) return ""
        return DATE_TIME_FORMAT.format(this)
    }

    private fun formatVehicle(
        model: String?,
        licensePlate: String?,
        regionCode: String?,
        payloadCapacity: Int? = null
    ): String {
        val parts = mutableListOf<String>()
        model?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        licensePlate?.takeIf { it.isNotBlank() }?.let { lp ->
            val region = regionCode?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
            parts.add("$lp$region")
        }
        payloadCapacity?.let { parts.add("г/п: $it кг") }
        return parts.joinToString(" • ")
    }

    private fun buildFileName(
        type: ReportType,
        period: ReportPeriod,
        format: ReportFormat
    ): String {
        val suffix = when (format) {
            ReportFormat.EXCEL -> "xlsx"
            ReportFormat.PDF -> "pdf"
            ReportFormat.CSV -> "csv"
        }
        val timestamp = OffsetDateTime.now().format(FILE_TIME_FORMAT)
        return "report_${type.name.lowercase()}_${period.name.lowercase()}_$timestamp.$suffix"
    }

    private fun ReportPeriod.displayName(): String {
        return when (this) {
            ReportPeriod.TODAY -> "Сегодня"
            ReportPeriod.LAST_7_DAYS -> "Последние 7 дней"
            ReportPeriod.LAST_30_DAYS -> "Последние 30 дней"
        }
    }

    private fun csvLine(values: List<String>): String {
        return values.joinToString(";") { escapeCsv(it) }
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.contains(";") || escaped.contains("\n") || escaped.contains("\r")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }

    private fun org.apache.poi.ss.usermodel.Sheet.addMergedRow(
        rowIndex: Int,
        value: String,
        style: org.apache.poi.ss.usermodel.CellStyle?,
        startCol: Int,
        endCol: Int
    ): Int {
        val row = createRow(rowIndex)
        val cell = row.createCell(startCol)
        cell.setCellValue(value)
        if (style != null) cell.cellStyle = style
        if (endCol > startCol) {
            addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(rowIndex, rowIndex, startCol, endCol))
        }
        return rowIndex
    }

    private companion object {
        private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        private val FILE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    }
}
