package com.github.radlance.autodispatch.driver.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.StatusWithColor
import com.github.radlance.autodispatch.common.utils.formatLicensePlate
import com.github.radlance.autodispatch.common.utils.toSimpleDateWithTimeString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.driver.common.presentation.DeliveryRoute
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
                        DefaultPointerSelectionContainer {
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
                        DefaultPointerSelectionContainer {
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
                    StatusWithColor(status = driverHistory.status)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                DefaultPointerSelectionContainer {
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
                            Text(
                                text = "${vehicle.model} (${
                                    formatLicensePlate(
                                        vehicle.licensePlate,
                                        vehicle.regionCode
                                    )
                                })"
                            )
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

                DeliveryRoute(
                    loadingPoint = driverHistory.loadingPoint.toStringAddress(),
                    unloadingPoint = driverHistory.unloadingPoint.toStringAddress()
                )
            }
        }
    }
}
