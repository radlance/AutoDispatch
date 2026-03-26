package com.github.radlance.autodispatch.vehicle.core.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.utils.abbreviateName
import com.github.radlance.autodispatch.common.utils.formatLicensePlate
import com.github.radlance.autodispatch.request.core.presentation.CustomPaginationDataTable
import com.github.radlance.autodispatch.uikit.theme.statusPalette
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.DataTableState
import com.seanproctor.datatable.TableColumnWidth
import kotlinx.coroutines.launch

@Composable
fun VehicleTable(
    vehicles: List<VehicleDetailed>,
    selectedVehicle: VehicleDetailed?,
    showPanel: Boolean,
    onVehicleClick: (VehicleDetailed) -> Unit,
    dataTableState: DataTableState,
    pageIndex: Int,
    pageSize: Int,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehicles.size) {
        dataTableState.verticalScrollState.scrollTo(0)
    }

    val highlight = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val animatedColors: Map<VehicleDetailed, Color> = vehicles.associateWith { vehicle ->
        val target = if (vehicle == selectedVehicle && showPanel) highlight else Color.Transparent
        animateColorAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 200),
            label = "rowColorAnimation_${vehicle.id}"
        ).value
    }

    CustomPaginationDataTable(
        modifier = modifier,
        dataTableState = dataTableState,
        columns = listOf(
            DataColumn(width = TableColumnWidth.Flex(0.3f)) {
                Text("№")
            },
            DataColumn(width = TableColumnWidth.Flex(3f)) {
                Text("Модель")
            },
            DataColumn(width = TableColumnWidth.Flex(2f)) {
                Text("Гос. номер")
            },
            DataColumn(width = TableColumnWidth.Flex(0.2f)) {
              Text("Грузоподъемность")
            },
            DataColumn(width = TableColumnWidth.Flex(1.2f)) {
                Text("Статус")
            },
            DataColumn(width = TableColumnWidth.Flex(4f)) {
                Text("Водитель")
            }
        )
    ) {
        vehicles.forEachIndexed { index, item ->
            row {
                backgroundColor = animatedColors[item] ?: Color.Transparent
                onClick = {
                    onVehicleClick(item)
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
                        text = item.model,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                cell {
                    Text(
                        text = formatLicensePlate(item.licensePlate, item.regionCode),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                cell {
                    Text(
                        text = "${item.payloadCapacity} кг",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                cell {
                   VehicleStatusWithColor(isAvailable = item.driverFullName != null)
                }

                cell {
                    Text(
                        text = item.driverFullName?.let { abbreviateName(it) } ?: "—",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
@Composable
fun VehicleStatusWithColor(isAvailable: Boolean, fontSize: TextUnit = TextUnit.Unspecified) {
    val palette = MaterialTheme.statusPalette
    val (bgColor, textColor) = if (isAvailable) {
        palette.successBg to palette.successText
    } else {
        palette.progressBg to palette.progressText
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = if (!isAvailable) "Свободен" else "Занят",
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
