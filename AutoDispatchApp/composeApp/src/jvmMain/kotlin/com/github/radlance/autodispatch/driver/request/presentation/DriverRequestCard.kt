package com.github.radlance.autodispatch.driver.request.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.common.utils.toSimpleDateString
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.driver.common.presentation.DeliveryRoute
import com.github.radlance.autodispatch.driver.request.domain.DriverRequest
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DriverRequestCard(
    selected: Boolean,
    onSelect: (Int) -> Unit,
    driverRequest: DriverRequest,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    )
    DisableSelection {
        Card(
            onClick = { onSelect(driverRequest.id) },
            modifier = modifier.fillMaxWidth(),
            border = BorderStroke(width = 1.dp, color = borderColor)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        DefaultPointerSelectionContainer {
                            Text(
                                text = buildAnnotatedString {
                                    append("Заявка ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(driverRequest.requestNumber)
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        InfoRow(
                            icon = Icons.Outlined.Person,
                            iconDesc = null,
                            text = driverRequest.customer.organizationName
                        )
                    }
                    InfoRow(
                        icon = Icons.Outlined.CalendarMonth,
                        iconDesc = null,
                        text = (driverRequest.updatedAt
                            ?: driverRequest.createdAt).toSimpleDateString()
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                InfoRow(
                    icon = Package2Icon,
                    iconDesc = null,
                    text = driverRequest.cargo.type.name
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Вес", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                            Text(text = driverRequest.cargo.weight.formatKg(), fontSize = 20.sp)
                        }
                    }
                    driverRequest.cargo.volume?.let { volume ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Объём",
                                    fontSize = 12.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Text(text = volume.formatM3(), fontSize = 20.sp)
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                DeliveryRoute(
                    loadingPoint = driverRequest.loadingPoint.toStringAddress(),
                    unloadingPoint = driverRequest.unloadingPoint.toStringAddress()
                )
            }
        }
    }
}