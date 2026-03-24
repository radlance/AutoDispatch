package com.github.radlance.autodispatch.statistics.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DownloadReportViewModel : BaseViewModel(), EventViewModel<DownloadReportEvent> {

    private val fieldsUiStateMutable = MutableStateFlow(DownloadReportUiState())
    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    override fun reduce(event: DownloadReportEvent) {
        val action = object : DownloadReportAction {
            override fun changeReportType(reportType: ReportType) {
                fieldsUiStateMutable.update { state ->
                    state.copy(reportType = reportType)
                }
            }

            override fun changeFileFormat(fileFormat: FileFormat) {
                fieldsUiStateMutable.update { state ->
                    state.copy(fileFormat = fileFormat)
                }
            }

            override fun changeReportPeriod(period: ReportPeriod) {
                fieldsUiStateMutable.update { state ->
                    state.copy(reportPeriod = period)
                }
            }
        }

        event.apply(action)
    }
}
