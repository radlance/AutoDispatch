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
import com.github.radlance.autodispatch.common.presentation.DriverStatusWithColor
import com.github.radlance.autodispatch.common.utils.formatLicensePlate
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
                Spacer(Modifier.height(4.dp))
                Text(
                    text = driverStats.vehicleModel?.let {
                        val plate = formatLicensePlate(
                            driverStats.vehicleLicensePlate.orEmpty(),
                            driverStats.vehicleRegionCode
                        )
                        "${driverStats.vehicleModel} • $plate • г/п: ${driverStats.vehiclePayloadCapacity} кг"
                    }
                        ?: "Нет автомобиля",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "График: ${formatSchedule(driverStats)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    modifier = Modifier.alpha(0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!driverStats.isWorkingNow) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Сейчас вне графика",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun formatSchedule(driverStats: DriverStats): String {
    if (driverStats.workSchedule.isEmpty()) return "не задан"

    val daySchedule = (1..7).associateWith { day ->
        val shifts = driverStats.workSchedule
            .filter { it.dayOfWeek == day }
            .sortedBy { it.startTime }
        if (shifts.isEmpty()) {
            "выходной"
        } else {
            shifts.joinToString(", ") { "${shortTime(it.startTime)}-${shortTime(it.endTime)}" }
        }
    }

    val parts = mutableListOf<String>()
    var startDay = 1
    var currentText = daySchedule.getValue(1)

    for (day in 2..7) {
        val nextText = daySchedule.getValue(day)
        if (nextText != currentText) {
            parts += "${dayRangeLabel(startDay, day - 1)} $currentText"
            startDay = day
            currentText = nextText
        }
    }
    parts += "${dayRangeLabel(startDay, 7)} $currentText"

    return parts.joinToString("; ")
}

private fun shortTime(value: String): String {
    val parts = value.split(":")
    if (parts.size != 2) return value
    val hour = parts[0].toIntOrNull()?.toString() ?: parts[0]
    val minute = parts[1]
    return "$hour:$minute"
}

private fun dayRangeLabel(startDay: Int, endDay: Int): String {
    val start = dayShort(startDay)
    val end = dayShort(endDay)
    return if (startDay == endDay) start else "$start-$end"
}

private fun dayShort(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "Пн"
        2 -> "Вт"
        3 -> "Ср"
        4 -> "Чт"
        5 -> "Пт"
        6 -> "Сб"
        7 -> "Вс"
        else -> dayOfWeek.toString()
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
