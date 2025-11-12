package com.github.radlance.autodispatch.delivery.details.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryRoute
import com.github.radlance.autodispatch.delivery.core.presentation.requestStatusColors
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openDialer
import com.github.radlance.autodispatch.reuqest.core.domain.Cargo
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.reuqest.core.domain.VehicleFilter
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DeliveryDetails(
    delivery: DeliveryDetailed,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = requestStatusColors(delivery.status.name)
    val context = getPlatformContext()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        StatusCard(
            status = delivery.status.name,
            createdAt = "${delivery.createdAt.date}, ${
                delivery.createdAt.hour.toString().padStart(2, '0')
            }:${
                delivery.createdAt.minute.toString().padStart(2, '0')
            }:${delivery.createdAt.second.toString().padStart(2, '0')}",
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        RouteCard(
            fromPoint = delivery.loadingPoint,
            toPoint = delivery.unloadingPoint,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        CargoCard(
            cargo = delivery.cargo,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        VehicleCard(
            vehicle = delivery.vehicle,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        ContactsCard(
            customer = delivery.customer,
            dispatcherFullName = delivery.dispatcherFullName,
            dispatcherPhoneNumber = delivery.dispatcherPhoneNumber,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        AdditionalInfoCard(
            description = delivery.transportationDescription,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth()
        )

        ActionButtons(
            onAcceptClick = { /* TODO */ },
            onContactClick = {
                openDialer(delivery.dispatcherPhoneNumber, context)
            },
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
    }
}

@Composable
private fun SectionHeader(
    text: String,
    icon: ImageVector,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Spacer(Modifier.width(12.dp))
            Text(text = text)
        }
    }
}

@Composable
private fun InfoRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBackgroundColor)
                .border(
                    width = 2.dp,
                    color = iconTint.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

@Composable
private fun StatusCard(
    status: String,
    createdAt: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "Статус доставки", fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor)
                    .border(
                        width = 2.dp,
                        color = contentColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = status,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
            InfoRow(
                title = "Дата и время создания",
                subtitle = createdAt,
                icon = Icons.Outlined.CalendarMonth,
                iconTint = contentColor,
                iconBackgroundColor = backgroundColor,
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}

@Composable
private fun RouteCard(
    fromPoint: String,
    toPoint: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Маршрут доставки",
                icon = Icons.Outlined.LocationOn,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            DeliveryRoute(
                fromPoint = fromPoint,
                toPoint = toPoint,
                color = contentColor,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}

@Composable
private fun CargoCard(
    cargo: Cargo,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Информация о грузе",
                icon = Package2Icon,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Тип груза",
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Icon(imageVector = DeployedCodeIcon, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(text = cargo.type.name)
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
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
                            Text(text = cargo.weight.formatKg(), fontSize = 20.sp)
                        }
                    }
                    cargo.volume?.let { volume ->
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
                cargo.description?.let { description ->
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                    Text(text = "Описание груза", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                    Spacer(Modifier.height(4.dp))
                    Text(text = description, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: VehicleFilter,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Транспорт",
                icon = AppIcon,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            InfoRow(
                title = vehicle.model,
                subtitle = "Гос. номер: ${vehicle.licensePlate}",
                icon = Icons.Outlined.LocationOn,
                iconTint = contentColor,
                iconBackgroundColor = backgroundColor,
                modifier = Modifier.padding(18.dp)
            )
        }
    }
}

@Composable
private fun ContactsCard(
    customer: Customer,
    dispatcherFullName: String,
    dispatcherPhoneNumber: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            SectionHeader(
                text = "Контакты",
                icon = Icons.Outlined.Person,
                color = contentColor,
                backgroundColor = backgroundColor
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Заказчик", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    title = customer.organizationName,
                    subtitle = customer.phoneNumber,
                    icon = Icons.Outlined.Person,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                Text(text = "Диспетчер", fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
                Spacer(Modifier.height(8.dp))
                InfoRow(
                    title = dispatcherFullName,
                    subtitle = dispatcherPhoneNumber,
                    icon = Icons.Outlined.Person,
                    iconTint = contentColor,
                    iconBackgroundColor = backgroundColor
                )
            }
        }
    }
}

@Composable
private fun AdditionalInfoCard(
    description: String?,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    description?.let {
        Card(modifier = modifier) {
            Column {
                SectionHeader(
                    text = "Дополнительная информация",
                    icon = Icons.AutoMirrored.Outlined.StickyNote2,
                    color = contentColor,
                    backgroundColor = backgroundColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = it,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onAcceptClick: () -> Unit,
    onContactClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Button(
            onClick = onAcceptClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = "Принять рейс")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onContactClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Outlined.Phone, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(text = "Связаться с диспетчером")
        }
    }
}