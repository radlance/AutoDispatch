package com.github.radlance.autodispatch.statistics.presentation

import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.ExpandedCustomDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadReportDialog(
    modifier: Modifier = Modifier,
    viewModel: DownloadReportViewModel = koinViewModel()
) {
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()
    var showDownloadReportDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (showDownloadReportDialog) {
        ExpandedCustomDialog(
            onDismissRequest = { showDownloadReportDialog = false },
            title = {
                Text(
                    text = "Выгрузить отчёт",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    DownloadReportFields(
                        onEvent = viewModel::reduce,
                        scrollState = scrollState,
                        fieldsUiState = fieldsUiState
                    )

                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(scrollState),
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                            .offset(x = 10.dp)
                    )
                }
            },
            buttons = {}
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
