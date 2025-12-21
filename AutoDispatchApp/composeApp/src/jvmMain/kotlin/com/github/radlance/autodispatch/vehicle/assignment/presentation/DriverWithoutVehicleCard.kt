package com.github.radlance.autodispatch.vehicle.assignment.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.request.assignment.presentation.RequestCountContainer
import com.github.radlance.autodispatch.vehicle.assignment.domain.DriverWithoutVehicle

@Composable
fun DriverWithoutVehicleCard(
    selected: Boolean,
    onSelect: (Int) -> Unit,
    driver: DriverWithoutVehicle,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    )

    DisableSelection {
        Card(
            onClick = { onSelect(driver.id) },
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
                            text = driver.fullName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = driver.phoneNumber,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val count = driver.totalDeliveries
                        RequestCountContainer(
                            count = count,
                            pluralizedLabel = pluralizeDeliveries(count),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun pluralizeDeliveries(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100

    val word = when {
        mod100 in 11..14 -> "доставок"
        mod10 == 1 -> "доставка"
        mod10 in 2..4 -> "доставки"
        else -> "доставок"
    }

    return "$count $word"
}