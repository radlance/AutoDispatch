package com.github.radlance.autodispatch.statistics.presentation

import com.github.radlance.autodispatch.common.presentation.BaseViewModel
import com.github.radlance.autodispatch.common.presentation.EventViewModel
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.toUiState
import com.github.radlance.autodispatch.statistics.domain.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DownloadReportViewModel(
    private val repository: ReportRepository
) : BaseViewModel(), EventViewModel<DownloadReportEvent> {

    private val fieldsUiStateMutable = MutableStateFlow(DownloadReportUiState())
    val fieldsUiState get() = fieldsUiStateMutable.asStateFlow()

    private val downloadStateMutable =
        MutableStateFlow<FetchResultUiState<String, String>>(FetchResultUiState.Idle)
    val downloadState get() = downloadStateMutable.asStateFlow()

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

            override fun downloadReport(targetPath: String) {
                downloadStateMutable.value = FetchResultUiState.Loading
                val fields = fieldsUiStateMutable.value

                handle(
                    background = {
                        repository.downloadReport(
                            reportType = fields.reportType,
                            format = fields.fileFormat,
                            period = fields.reportPeriod,
                            targetPath = targetPath
                        )
                    }
                ) {
                    downloadStateMutable.value = it.toUiState()
                }
            }

            override fun resetDownloadState() {
                downloadStateMutable.value = FetchResultUiState.Idle
            }
        }

        event.apply(action)
    }
}
