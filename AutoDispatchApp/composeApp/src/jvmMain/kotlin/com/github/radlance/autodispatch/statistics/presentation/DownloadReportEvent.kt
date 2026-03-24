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

    class DownloadReport(private val targetPath: String) : DownloadReportEvent {

        override fun apply(action: DownloadReportAction) = action.downloadReport(targetPath)
    }

    object ResetDownloadState : DownloadReportEvent {

        override fun apply(action: DownloadReportAction) = action.resetDownloadState()
    }
}

interface DownloadReportAction {

    fun changeReportType(reportType: ReportType)

    fun changeFileFormat(fileFormat: FileFormat)

    fun changeReportPeriod(period: ReportPeriod)

    fun downloadReport(targetPath: String)

    fun resetDownloadState()
}
