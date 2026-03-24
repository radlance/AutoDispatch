package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DownloadReportFields(
    onEvent: (DownloadReportEvent) -> Unit,
    fieldsUiState: DownloadReportUiState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Тип отчёта", fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        ReportType.entries.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(DownloadReportEvent.ChangeReportType(type)) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = fieldsUiState.reportType == type,
                    onClick = { onEvent(DownloadReportEvent.ChangeReportType(type)) }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = type.displayName, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(12.dp))

        Text(text = "Формат файла", fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        FileFormat.entries.forEach { format ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(DownloadReportEvent.ChangeFileFormat(format)) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = fieldsUiState.fileFormat == format,
                    onClick = { onEvent(DownloadReportEvent.ChangeFileFormat(format)) }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = format.displayName, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(12.dp))

        Text(text = "Период отчёта", fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        ReportPeriod.entries.forEach { period ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(DownloadReportEvent.ChangeReportPeriod(period)) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = fieldsUiState.reportPeriod == period,
                    onClick = { onEvent(DownloadReportEvent.ChangeReportPeriod(period)) }
                )
                Spacer(Modifier.width(12.dp))
                Text(text = period.displayName, fontSize = 16.sp)
            }
        }
    }
}
