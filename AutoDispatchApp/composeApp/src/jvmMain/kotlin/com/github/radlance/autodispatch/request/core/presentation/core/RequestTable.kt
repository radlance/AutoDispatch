package com.github.radlance.autodispatch.request.core.presentation.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.car
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.created
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.request
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.controlpanel.presentation.abbreviateName
import com.github.radlance.autodispatch.request.core.domain.Request
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestTable(
    requests: List<Request>,
    selectedRequest: Request?,
    showPanel: Boolean,
    onRequestClick: (Request) -> Unit,
    dataTableState: DataTableState,
    pageIndex: Int,
    pageSize: Int,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(requests.size) {
        dataTableState.verticalScrollState.scrollTo(0)
    }

    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val animatedColors: Map<Request, Color> = requests.associateWith { req ->
        val target = if (req == selectedRequest && showPanel) highlight else Color.Transparent
        animateColorAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 200),
            label = "rowColorAnimation_${req.requestNumber}"
        ).value
    }

    CustomPaginationDataTable(
        modifier = modifier,
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
                backgroundColor = animatedColors[item] ?: Color.Transparent
                onClick = {
                    onRequestClick(item)
                    scope.launch {
                        dataTableState.horizontalScrollState.scrollTo(0)
                    }
                }
                cell {
                    Text(
                        text = (pageIndex * pageSize + index + 1).toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.requestNumber,
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
                        text = item.cargoTypeName,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.createdAt.date.toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    StatusWithColor(status = item.statusName)
                }
                cell {
                    Text(text = item.driverFullName?.let { abbreviateName(it) } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text(
                        text = item.vehicleInfo?.ifEmpty { "—" } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}