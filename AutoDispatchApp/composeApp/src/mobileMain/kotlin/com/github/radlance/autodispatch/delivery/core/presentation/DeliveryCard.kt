package com.github.radlance.autodispatch.delivery.core.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DeliveryCard(
    navigateToDeliveryDetails: () -> Unit,
    delivery: Delivery,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = requestStatusColors(delivery.status.name)

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = navigateToDeliveryDetails
    ) {
        Column {
            DeliveryHeader(navigateToDeliveryDetails, delivery, backgroundColor, contentColor)
            DeliveryRoute(delivery, contentColor, Modifier.padding(horizontal = 18.dp))
            DeliveryDivider(contentColor)
            DeliveryFooter(delivery, backgroundColor)
        }
    }
}

@Composable
private fun DeliveryHeader(
    navigateToRequestDetails: () -> Unit,
    delivery: Delivery,
    backgroundColor: Color,
    contentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(18.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(
                    width = 2.dp,
                    color = contentColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = Package2Icon,
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                tint = contentColor
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f).padding(horizontal = 18.dp)) {
            Text(buildAnnotatedString {
                append("Доставка ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(delivery.requestNumber) }
            })
            Spacer(Modifier.height(4.dp))
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
                    text = delivery.status.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        IconButton(
            onClick = navigateToRequestDetails,
            modifier = Modifier.offset(x = 10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DeliveryRoute(request: Delivery, color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.height(130.dp).width(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Circle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Canvas(modifier = Modifier.height(90.dp).width(2.dp)) {
                drawLine(
                    color = color,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
            Icon(
                Icons.Default.Place,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.height(130.dp)) {
            RouteText("Откуда", request.loadingPoint)
            Spacer(Modifier.weight(1f))
            RouteText("Куда", request.unloadingPoint)
        }
    }
}

@Composable
private fun RouteText(title: String, location: String) {
    Text(text = title, fontSize = 12.sp, modifier = Modifier.offset(y = (-4).dp).alpha(0.7f))
    Text(text = location, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun DeliveryDivider(color: Color) {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().offset(y = 2.dp),
        thickness = 2.dp,
        color = color.copy(alpha = 0.2f)
    )
}

@Composable
private fun DeliveryFooter(delivery: Delivery, backgroundColor: Color) {
    Box(
        modifier = Modifier.fillMaxWidth().background(backgroundColor.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(18.dp)
        ) {
            CardSection("Груз", delivery.cargoTypeName)
            CardSection("Вес", delivery.cargoWeight.formatKg())
            CardSection(
                "Обновлена",
                "${(delivery.updatedAt ?: delivery.createdAt).day.toString().padStart(2, '0')}." +
                        "${
                            (delivery.updatedAt ?: delivery.createdAt).month.ordinal.inc()
                                .toString().padStart(2, '0')
                        }, " +
                        "${
                            (delivery.updatedAt ?: delivery.createdAt).hour.toString()
                                .padStart(2, '0')
                        }:${
                            (delivery.updatedAt ?: delivery.createdAt).minute.toString()
                                .padStart(2, '0')
                        }"
            )
        }
    }
}

@Composable
private fun CardSection(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 12.sp, modifier = Modifier.alpha(0.7f))
        Text(text = subtitle, fontSize = 14.sp)
    }
}

@Composable
fun requestStatusColors(status: String) =
    if (status == "Назначена") {
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    } else MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
