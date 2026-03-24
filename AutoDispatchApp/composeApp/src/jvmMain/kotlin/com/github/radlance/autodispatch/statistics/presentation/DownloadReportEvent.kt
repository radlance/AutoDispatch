package com.github.radlance.autodispatch.statistics.presentation

import com.github.radlance.autodispatch.common.presentation.Event

interface DownloadReportEvent : Event {

    fun apply(action: DownloadReportAction)

    class ChangeReportType(private val reportType: ReportType) : DownloadReportEvent {

        override fun apply(action: DownloadReportAction) = action.changeReportType(reportType)
    }

    class ChangeFileFormat(private val fileFormat: FileFormat) : DownloadReportEvent {

        override fun apply(action: DownloadReportAction) = action.changeFileFormat(fileFormat)
    }

    class ChangeReportPeriod(private val period: ReportPeriod) : DownloadReportEvent {

        override fun apply(action: DownloadReportAction) = action.changeReportPeriod(period)
    }
}

interface DownloadReportAction {

    fun changeReportType(reportType: ReportType)

    fun changeFileFormat(fileFormat: FileFormat)

    fun changeReportPeriod(period: ReportPeriod)
}
