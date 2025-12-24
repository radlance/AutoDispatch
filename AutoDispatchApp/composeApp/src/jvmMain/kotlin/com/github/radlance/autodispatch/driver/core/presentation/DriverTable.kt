package com.github.radlance.autodispatch.driver.core.presentation

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
import autodispatch.composeapp.generated.resources.vehicle
import com.github.radlance.autodispatch.common.utils.abbreviateName
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.request.core.presentation.CustomPaginationDataTable
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun DriverTable(
    drivers: List<Driver>,
    selectedDriver: Driver?,
    showPanel: Boolean,
    onDriverClick: (Driver) -> Unit,
    dataTableState: DataTableState,
    pageIndex: Int,
    pageSize: Int,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(drivers.size) {
        dataTableState.verticalScrollState.scrollTo(0)
    }

    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val animatedColors: Map<Driver, Color> = drivers.associateWith { driver ->
        val target = if (driver == selectedDriver && showPanel) highlight else Color.Transparent
        animateColorAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 200),
            label = "rowColorAnimation_${driver.id}"
        ).value
    }

    CustomPaginationDataTable(
        modifier = modifier,
        dataTableState = dataTableState,
        columns = listOf(
            DataColumn(width = TableColumnWidth.Flex(0.3f)) {
                Text("№")
            },
            DataColumn(width = TableColumnWidth.Flex(4f)) {
                Text("ФИО")
            },
            DataColumn(width = TableColumnWidth.Flex(3f)) {
                Text("Телефон")
            },
            DataColumn(width = TableColumnWidth.Flex(1.2f)) {
                Text("Статус")
            },
            DataColumn(width = TableColumnWidth.Flex(3f)) {
                Text(stringResource(Res.string.vehicle))
            },
            DataColumn(width = TableColumnWidth.Flex(0.5f)) {
                Text("Доставок")
            }
        )
    ) {
        drivers.forEachIndexed { index, item ->
            row {
                backgroundColor = animatedColors[item] ?: Color.Transparent
                onClick = {
                    onDriverClick(item)
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
                        text = abbreviateName(item.fullName),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.phoneNumber,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    DriverStatusWithColor(status = item.status.name)
                }
                cell {
                    Text(
                        text = item.vehicle?.let { "${it.model} (${it.licensePlate})" } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                cell {
                    Text(
                        text = item.deliveriesStats.totalCount.toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}