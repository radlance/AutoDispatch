package com.github.radlance.autodispatch.request.assignment.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats

@Composable
fun DriverAssignmentCard(
    selected: Boolean,
    onSelect: (Int) -> Unit,
    driverStats: DriverStats,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    )
    DisableSelection {
        Card(
            onClick = { onSelect(driverStats.driverId) },
            modifier = modifier.fillMaxWidth(),
            border = BorderStroke(width = 1.dp, color = borderColor)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = driverStats.driverName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = driverStats.phoneNumber.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = driverStats.vehicleModel?.let { "${driverStats.vehicleModel} • ${driverStats.vehicleLicensePlate} • г/п: ${driverStats.vehiclePayloadCapacity} кг" }
                                ?: "Нет автомобиля",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val count = driverStats.totalAssignedRequests.toInt()
                        RequestCountContainer(
                            count = count,
                            pluralizedLabel = pluralizeRequests(count),
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.width(12.dp))
                        driverStats.vehicleModel?.let {
                            DriverStatusWithColor(
                                status = driverStats.driverStatus,
                                fontSize = 12.sp
                            )
                        } ?: Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "Без авто",
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverStatusWithColor(status: String?, fontSize: TextUnit = TextUnit.Unspecified) {
    val (bgColor, textColor) = when (status) {

        "Свободен" ->
            MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer

        "В рейсе" ->
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer

        "Не на смене" ->
            MaterialTheme.colorScheme.surfaceVariant to
                    MaterialTheme.colorScheme.onSurfaceVariant

        else ->
            MaterialTheme.colorScheme.surface to
                    MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = status ?: "-",
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RequestCountContainer(count: Int, pluralizedLabel: String, fontSize: TextUnit = TextUnit.Unspecified) {

    val (bgColor, textColor) = when (count) {
        0 ->
            MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer

        in 1..2 ->
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer

        in 3..4 ->
            MaterialTheme.colorScheme.tertiaryContainer to
                    MaterialTheme.colorScheme.onTertiaryContainer

        else -> MaterialTheme.colorScheme.errorContainer to
                MaterialTheme.colorScheme.onErrorContainer
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
    ) {
        Text(
            text = pluralizedLabel,
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


private fun pluralizeRequests(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100

    val word = when {
        mod100 in 11..14 -> "заявок"
        mod10 == 1 -> "заявка"
        mod10 in 2..4 -> "заявки"
        else -> "заявок"
    }

    return "$count $word"
}

