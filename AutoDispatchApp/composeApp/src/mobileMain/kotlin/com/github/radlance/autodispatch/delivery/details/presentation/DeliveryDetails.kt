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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.delivery.core.presentation.requestStatusColors
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DeliveryDetails(
    request: Request,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = requestStatusColors(request.status.name)
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
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
                        text = request.status.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(
                                width = 2.dp,
                                color = contentColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(
                            text = "Дата и время создания",
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Text(
                            text = "${request.createdAt.date}, ${
                                request.createdAt.hour.toString().padStart(2, '0')
                            }:${
                                request.createdAt.minute.toString().padStart(2, '0')
                            }:${request.createdAt.second.toString().padStart(2, '0')}",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(backgroundColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn, contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Маршрут доставки")
                    }
                }
//                DeliveryRoute(request, contentColor, modifier = Modifier.padding(18.dp))
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(backgroundColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Icon(
                            imageVector = Package2Icon,
                            contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Информация о грузе")
                    }
                }
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
                        Text(text = request.cargo.type.name)
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
                                Text(
                                    text = "Вес", fontSize = 12.sp,
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Text(
                                    text = request.cargo.weight.formatKg(), fontSize = 20.sp
                                )
                            }
                        }
                        request.cargo.volume?.let { volume ->
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
                                        text = "Объём", fontSize = 12.sp,
                                        modifier = Modifier.alpha(0.7f)
                                    )
                                    Text(
                                        text = volume.formatM3(), fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    }
                    request.cargo.description?.let { description ->
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                        )
                        Text(
                            text = "Описание груза",
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(backgroundColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map, contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Адреса")
                    }
                }
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Точка погрузки",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .border(
                                    width = 2.dp,
                                    color = contentColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = contentColor
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(text = request.loadingPoint)
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                    )
                    Text(
                        text = "Точка выгрузки",
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .border(
                                    width = 2.dp,
                                    color = contentColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = contentColor
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(text = request.unloadingPoint)
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(backgroundColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Icon(
                            imageVector = AppIcon, contentDescription = null,
                            tint = contentColor
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = "Транспорт")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(18.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(
                                width = 2.dp,
                                color = contentColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = contentColor
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        val model = request.vehicleInfo!!.substringBeforeLast("(").trim()
                        val plate = request.vehicleInfo.substringAfterLast("(").removeSuffix(")").trim()
                        Text(
                            text = model,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Гос. номер: $plate",
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
        }
    }
}