package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.choice_driver
import autodispatch.composeapp.generated.resources.choice_vehicle
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.vehicle
import com.github.radlance.autodispatch.request.assignment.domain.DriverStats
import com.github.radlance.autodispatch.request.assignment.domain.VehicleStats
import com.github.radlance.autodispatch.request.change.presentation.CustomDropDownMenu
import org.jetbrains.compose.resources.stringResource

@Composable
fun DriverAssignmentFields(
    driversStats: List<DriverStats>,
    vehiclesStats: List<VehicleStats>,
    fieldsState: AssignmentFieldsState,
    onEvent: (AssignmentEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomDropDownMenu(
            label = stringResource(Res.string.driver),
            selectedOption = fieldsState.selectedDriverStats?.driverName,
            filterOptions = driversStats.map { it.driverName },
            onOptionSelected = { option ->
                onEvent(
                    AssignmentEvent.ChangeDriverStats(driversStats.first { option == it.driverName })
                )
            },
            hint = stringResource(Res.string.choice_driver),
            modifier = Modifier.fillMaxWidth(),
            isRequired = true
        ) { optionLabel ->
            val currentOption = driversStats.first {
                it.driverName == optionLabel
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = currentOption.driverName,
                    color = MaterialTheme.colorScheme.onSurface
                )
                RequestCountContainer(currentOption.totalAssignedRequests.toInt())
                DriverStatusWithColor(status = currentOption.status)
            }
        }
        Spacer(Modifier.height(16.dp))
        CustomDropDownMenu(
            label = stringResource(Res.string.vehicle),
            selectedOption = fieldsState.selectedVehicleStats?.model,
            filterOptions = vehiclesStats.map { it.model },
            onOptionSelected = { option ->
                onEvent(
                    AssignmentEvent.ChangeVehicleStats(vehiclesStats.first { option == it.model })
                )
            },
            hint = stringResource(Res.string.choice_vehicle),
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            isRequired = true
        ) { optionLabel ->
            val currentOption = vehiclesStats.first {
                it.model == optionLabel
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${currentOption.model} (${currentOption.licencePlate})",
                    color = MaterialTheme.colorScheme.onSurface
                )
                VehicleStatusWithColor(status = currentOption.vehicleStatus)
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
fun VehicleStatusWithColor(status: String?, fontSize: TextUnit = TextUnit.Unspecified) {
    val (bgColor, textColor) = when (status) {

        "Доступен" ->
            MaterialTheme.colorScheme.primaryContainer to
                    MaterialTheme.colorScheme.onPrimaryContainer

        "В рейсе" ->
            MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer

        "На ТО" ->
            MaterialTheme.colorScheme.tertiaryContainer to
                    MaterialTheme.colorScheme.onTertiaryContainer

        "Недоступен" ->
            MaterialTheme.colorScheme.errorContainer to
                    MaterialTheme.colorScheme.onErrorContainer

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
fun RequestCountContainer(count: Int, fontSize: TextUnit = TextUnit.Unspecified) {

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
            text = pluralizeRequests(count),
            maxLines = 1,
            color = textColor,
            overflow = TextOverflow.Ellipsis,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


fun pluralizeRequests(count: Int): String {
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

