package com.github.radlance.autodispatch.driver.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.StatusWithColor
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.driver.history.domain.DriverHistory
import com.github.radlance.autodispatch.request.core.presentation.routeText
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DriverHistoryCard(
    driverHistory: DriverHistory,
    modifier: Modifier = Modifier
) {
    DisableSelection {
        Card(modifier = modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {

                        SelectionContainer(
                            modifier = Modifier.pointerHoverIcon(
                                PointerIcon.Default,
                                overrideDescendants = true
                            )
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Доставка ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(driverHistory.requestNumber)
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        SelectionContainer(
                            modifier = Modifier.pointerHoverIcon(
                                PointerIcon.Default,
                                overrideDescendants = true
                            )
                        ) {

                            InfoRow(
                                icon = Icons.Outlined.LocationOn,
                                iconDesc = null,
                                text = routeText(
                                    driverHistory.originCity,
                                    driverHistory.destinationCity
                                )
                            )
                        }
                    }
                    StatusWithColor(status = driverHistory.status.name)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                SelectionContainer(
                    modifier = Modifier.pointerHoverIcon(
                        PointerIcon.Default,
                        overrideDescendants = true
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            InfoRow(
                                icon = Icons.Outlined.CalendarMonth,
                                iconDesc = null,
                                text = "Дата назначения"
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = driverHistory.assignedAt.toSimpleDateWithTimeString())
                            Spacer(Modifier.height(32.dp))
                            InfoRow(
                                icon = AppIcon,
                                iconDesc = null,
                                text = "Транспорт"
                            )
                            Spacer(Modifier.height(8.dp))
                            val vehicle = driverHistory.vehicle
                            Text(text = "${vehicle.model} (${vehicle.licensePlate})")
                        }

                        Column {
                            InfoRow(
                                icon = Icons.Outlined.CheckCircleOutline,
                                iconDesc = null,
                                text = "Дата завершения"
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = driverHistory.completedAt.toSimpleDateWithTimeString())
                            Spacer(Modifier.height(32.dp))
                            InfoRow(
                                icon = Package2Icon,
                                iconDesc = null,
                                text = "Тип груза"
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(text = driverHistory.cargoTypeName)
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                SelectionContainer(
                    modifier = Modifier.pointerHoverIcon(
                        PointerIcon.Default,
                        overrideDescendants = true
                    )
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier
                                .width(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .width(2.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )

                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Погрузка",
                                    fontSize = 12.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = driverHistory.loadingPoint.toStringAddress(),
                                    fontSize = 14.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            Column {
                                Text(
                                    text = "Разгрузка",
                                    fontSize = 12.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = driverHistory.unloadingPoint.toStringAddress(),
                                    fontSize = 14.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

