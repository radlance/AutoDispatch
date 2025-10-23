package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.all_requests
import autodispatch.composeapp.generated.resources.car
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.date
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.controlpanel.presentation.abbreviateName
import com.github.radlance.autodispatch.profile.domain.User
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestViewModel = koinViewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("") }
    val requestsUiState by viewModel.loadRequestUiState.collectAsState()

    requestsUiState.Reduce(
        onLoading = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        },
        onSuccess = { request ->
            val filterOptions =
                listOf(stringResource(Res.string.all_requests)) + request.cargoTypes.map { it.name }

            Column(modifier = modifier.fillMaxSize()) {
                if (selectedOption.isEmpty()) {
                    selectedOption = filterOptions.first()
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        SearchField(
                            query = query,
                            onQueryChange = { query = it },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        RequestCategories(
                            selectedOption = selectedOption,
                            filterOptions = filterOptions,
                            onOptionSelected = { selectedOption = it },
                        )
                        Spacer(Modifier.width(16.dp))
                        Button(onClick = {}) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(Res.string.create_request))
                        }
                    }
                }
                var selectedRow by remember { mutableStateOf<Int?>(null) }
                CustomPaginationDataTable(
                    modifier = Modifier.fillMaxWidth(),
                    state = rememberPaginatedDataTableState(10),
                    columns = listOf(
                        DataColumn(width = TableColumnWidth.Flex(0.3f)) {
                            Text("№")
                        },
                        DataColumn(width = TableColumnWidth.Flex(3f)) {
                            Text(stringResource(Res.string.route))
                        },
                        DataColumn {
                            Text(stringResource(Res.string.cargo_type))
                        },
                        DataColumn(width = TableColumnWidth.Flex(0.5f)) {
                            Text(stringResource(Res.string.date))
                        },
                        DataColumn {
                            Text(stringResource(Res.string.status))
                        },
                        DataColumn(width = TableColumnWidth.Flex(1.5f)) {
                            Text(stringResource(Res.string.driver))
                        },
                        DataColumn(width = TableColumnWidth.Flex(3f)) {
                            Text(stringResource(Res.string.car))
                        },
                    )
                ) {
                    request.requests.forEachIndexed { index, item ->
                        row {
                            onClick = { selectedRow = 0 }
                            cell {
                                Text(
                                    text = (index + 1).toString(),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            cell {
                                Text(
                                    text = "${item.origin} → ${item.destination}",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            cell {
                                Text(
                                    text = item.cargoTypeName ?: "-",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            cell {
                                Text(
                                    text = item.createdAt?.date?.toString() ?: "-",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            cell {
                                StatusWithColor(status = item.statusName)
                            }
                            cell {
                                Text(text = item.driverFullName?.let { abbreviateName(it) } ?: "-",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis)
                            }
                            cell {
                                Text(
                                    text = item.vehicleInfo ?: "-",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        onError = {
            ErrorMessage(
                message = it,
                onRetry = {
                    viewModel.loadRequests()
                    if (loadProfileUiState is FetchResultUiState.Error) {
                        onReloadProfile()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
private fun StatusWithColor(status: String?) {
    val (bgColor, textColor) = when (status) {
        "Ожидает" -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        "Назначена" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "В пути" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "Завершена" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Отменена" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = status ?: "-",
            maxLines = 2,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

