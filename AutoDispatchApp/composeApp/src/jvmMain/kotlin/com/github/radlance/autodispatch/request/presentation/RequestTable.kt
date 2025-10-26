package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.car
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.created
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.request
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.controlpanel.presentation.abbreviateName
import com.github.radlance.autodispatch.request.domain.Request
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.PaginatedDataTableState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestTable(
    requests: List<Request>,
    onRequestClick: (Request) -> Unit,
    state: PaginatedDataTableState,
    dataTableState: DataTableState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(requests.size) {
        dataTableState.verticalScrollState.scrollTo(0)
    }

    CustomPaginationDataTable(
        modifier = modifier,
        state = state,
        dataTableState = dataTableState,
        columns = listOf(
            DataColumn(width = TableColumnWidth.Flex(0.3f)) {
                Text("№")
            },
            DataColumn(width = TableColumnWidth.Flex(0.3f)) {
                Text(stringResource(Res.string.request))
            },
            DataColumn(width = TableColumnWidth.Flex(3f)) {
                Text(stringResource(Res.string.route))
            },
            DataColumn {
                Text(stringResource(Res.string.cargo_type))
            },
            DataColumn(width = TableColumnWidth.Flex(0.5f)) {
                Text(stringResource(Res.string.created))
            },
            DataColumn(width = TableColumnWidth.Flex(1.2f)) {
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
        requests.forEachIndexed { index, item ->
            row {
                onClick = {
                    onRequestClick(item)
                    scope.launch {
                        dataTableState.horizontalScrollState.scrollTo(0)
                    }
                }
                cell {
                    Text(
                        text = (index + 1).toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.requestNumber!!,
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
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}