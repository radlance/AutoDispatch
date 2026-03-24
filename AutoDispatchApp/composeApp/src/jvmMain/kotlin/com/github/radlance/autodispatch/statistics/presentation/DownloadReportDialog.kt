package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.ExpandedCustomDialog
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.koin.compose.viewmodel.koinViewModel
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DownloadReportDialog(
    modifier: Modifier = Modifier,
    viewModel: DownloadReportViewModel = koinViewModel()
) {
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    var showDownloadReportDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val isLoading = downloadState is FetchResultUiState.Loading
    val error = (downloadState as? FetchResultUiState.Error)?.error

    if (showDownloadReportDialog) {
        ExpandedCustomDialog(
            allowDismiss = !isLoading,
            onDismissRequest = { showDownloadReportDialog = false },
            onFinish = { viewModel.reduce(DownloadReportEvent.ResetDownloadState) },
            title = { _ ->
                Text(
                    text = "Выгрузить отчёт",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            },
            content = { requestDismiss ->
                Column(modifier = Modifier.fillMaxSize()) {
                    error?.let {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        DownloadReportFields(
                            onEvent = viewModel::reduce,
                            scrollState = scrollState,
                            fieldsUiState = fieldsUiState
                        )

                        if (!isLoading) {
                            VerticalScrollbar(
                                adapter = rememberScrollbarAdapter(scrollState),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterEnd)
                                    .offset(x = 10.dp)
                            )
                        }

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AlertDialogDefaults.containerColor),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                LaunchedEffect(downloadState) {
                    if (downloadState is FetchResultUiState.Success) {
                        requestDismiss()
                    }
                }
            },
            buttons = { requestDismiss ->
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = requestDismiss,
                    enabled = !isLoading
                ) {
                    Text(text = "Отмена")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        val suggestedName = buildDefaultFileName(fieldsUiState)
                        val path = selectSavePath(suggestedName, fieldsUiState.fileFormat)
                        if (path != null) {
                            viewModel.reduce(DownloadReportEvent.DownloadReport(path))
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(text = "Выгрузить")
                }
            }
        )
    }
    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = modifier.padding(all = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Выгрузка отчётов",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Экспортируйте данные в удобном формате: Excel, PDF или CSV",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier.alpha(0.8f)
                )
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { showDownloadReportDialog = true }
            ) {
                Icon(Icons.Outlined.Upload, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Выгрузить отчёт")
            }
        }
    }
}

private fun buildDefaultFileName(state: DownloadReportUiState): String {
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
    val type = state.reportType.name.lowercase()
    val period = state.reportPeriod.name.lowercase()
    return "report_${type}_${period}_$timestamp.${state.fileFormat.extension}"
}

private fun selectSavePath(defaultFileName: String, format: FileFormat): String? {
    val dialog = FileDialog(null as Frame?, "Сохранить отчёт", FileDialog.SAVE).apply {
        file = defaultFileName
        filenameFilter = FilenameFilter { _, name ->
            name.endsWith(".${format.extension}", ignoreCase = true)
        }
    }

    dialog.isVisible = true

    val directory = dialog.directory ?: return null
    val file = dialog.file ?: return null
    var path = File(directory, file).absolutePath
    if (!path.lowercase().endsWith(".${format.extension}")) {
        path += ".${format.extension}"
    }
    return path
}
