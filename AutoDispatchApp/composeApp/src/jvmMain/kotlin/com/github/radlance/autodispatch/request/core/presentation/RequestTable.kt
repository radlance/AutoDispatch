package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.created
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.request
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import autodispatch.composeapp.generated.resources.vehicle
import com.github.radlance.autodispatch.common.presentation.StatusWithColor
import com.github.radlance.autodispatch.common.utils.abbreviateName
import com.github.radlance.autodispatch.common.utils.toSimpleDateString
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
            DataColumn(width = TableColumnWidth.Flex(0.5f)) {
                Text("Документы")
            },
            DataColumn(width = TableColumnWidth.Flex(1.5f)) {
                Text(stringResource(Res.string.driver))
            },
            DataColumn(width = TableColumnWidth.Flex(3f)) {
                Text(stringResource(Res.string.vehicle))
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
                        text = item.cargo.type.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.createdAt.toSimpleDateString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    StatusWithColor(status = item.status.name)
                }
                cell {
                    DocumentsStatusWithColor(status = item.status.name)
                }
                cell {
                    Text(text = item.driverFullName?.let { abbreviateName(it) } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                }
                cell {
                    Text(
                        text = item.vehicle?.let { "${it.model} (${it.licensePlate})" } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentsStatusWithColor(status: String?) {
    val (bgColor, textColor, icon) = when (status) {
        "На проверке" -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Outlined.Schedule
        )


        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Outlined.ErrorOutline
        )
    }

    if (status == "На проверке" || status == "Отклонена") {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clip(CircleShape)
                .background(bgColor)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.padding(4.dp)
            )
        }
    } else Text(text = "—")
}