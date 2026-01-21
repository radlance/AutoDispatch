package com.github.radlance.autodispatch.delivery.core.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.delivery.core.domain.Delivery
import com.github.radlance.autodispatch.platform.MapPoint
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.GlobalLocationPinIcon
import com.github.radlance.autodispatch.uikit.vector.Package2Icon

@Composable
fun DeliveryCard(
    navigateToDeliveryDetails: () -> Unit,
    onContinueDeliveryClick: () -> Unit,
    delivery: Delivery,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = deliveryStatusColors(delivery.status)

    val customTextSelectionColors = TextSelectionColors(
        handleColor = contentColor,
        backgroundColor = backgroundColor.copy(alpha = 0.5f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = navigateToDeliveryDetails
        ) {
            Column {
                DeliveryHeader(navigateToDeliveryDetails, delivery, backgroundColor, contentColor)
                DeliveryRoute(
                    fromPoint = delivery.loadingPoint.toStringAddress(),
                    toPoint = delivery.unloadingPoint.toStringAddress(),
                    color = contentColor,
                    Modifier.padding(horizontal = 18.dp)
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(backgroundColor.copy(alpha = 0.3f))
                ) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 2.dp,
                            color = contentColor.copy(0.3f)
                        )
                        DeliveryFooter(delivery)
                        if (delivery.status.id == 3) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 2.dp,
                                color = contentColor.copy(0.3f)
                            )
                        }
                    }
                }
                if (delivery.status.id == 3) {
                    DeliveryRouteAction(
                        onContinueDeliveryClick = onContinueDeliveryClick,
                        backgroundColor = backgroundColor,
                        contentColor = contentColor
                    )
                }
            }
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
            val icon = when (delivery.status.id) {
                2 -> Package2Icon
                3 -> AppIcon
                6 -> Icons.Outlined.Schedule
                4 -> Icons.Outlined.CheckCircle
                5 -> Icons.Outlined.Cancel
                else -> Icons.Outlined.Block
            }
            Icon(
                imageVector = icon,
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
                    text = delivery.status.title,
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
fun DeliveryRoute(
    fromPoint: String,
    toPoint: String,
    color: Color,
    modifier: Modifier = Modifier,
    showOpenMapButton: Boolean = false
) {
    var selectedAddress by remember { mutableStateOf<String?>(null) }
    selectedAddress?.let {
        MapPoint(address = it, onDismiss = { selectedAddress = null })
    }

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
                tint = color,
                modifier = Modifier.size(16.dp)
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .width(2.dp)
                    .background(color)
            )

            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = color,
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
                    text = "Откуда",
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                SelectionContainer {
                    Text(
                        text = fromPoint,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showOpenMapButton) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OpenMapButton(color = color, onClick = { selectedAddress = fromPoint })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Column {
                Text(
                    text = "Куда",
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                SelectionContainer {
                    Text(
                        text = toPoint,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showOpenMapButton) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OpenMapButton(color = color, onClick = { selectedAddress = toPoint })
                }
            }
        }
    }
}

@Composable
fun OpenMapButton(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GlobalLocationPinIcon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "Открыть на карте",
            fontSize = 13.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DeliveryFooter(delivery: Delivery) {
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

@Composable
private fun DeliveryRouteAction(
    onContinueDeliveryClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {

    OutlinedButton(
        onClick = onContinueDeliveryClick,
        modifier = Modifier.fillMaxWidth().padding(18.dp),
        border = BorderStroke(width = 1.dp, color = contentColor.copy(alpha = 0.2f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Icon(imageVector = Icons.Outlined.NearMe, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(text = "Продолжить доставку")
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
fun deliveryStatusColors(status: RequestStatus) =
    when (status) {
        RequestStatus.Assigned -> {
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        }

        RequestStatus.Canceled, RequestStatus.Rejected -> {
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        }

        RequestStatus.OnCheck -> {
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer
        }

        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
