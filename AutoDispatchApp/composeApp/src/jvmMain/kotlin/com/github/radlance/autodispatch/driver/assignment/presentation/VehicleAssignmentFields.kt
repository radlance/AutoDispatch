package com.github.radlance.autodispatch.driver.assignment.presentation

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.driver
import com.github.radlance.autodispatch.request.change.presentation.CustomDropDownMenu
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import org.jetbrains.compose.resources.stringResource

@Composable
fun VehicleAssignmentFields(
    vehicles: List<Vehicle>,
    fieldsState: VehicleAssignmentFieldsState,
    onEvent: (VehicleAssignmentEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomDropDownMenu(
            label = stringResource(Res.string.driver),
            selectedOption = fieldsState.selectedVehicle?.model,
            filterOptions = vehicles.map { it.model },
            onOptionSelected = { option ->
                onEvent(
                    VehicleAssignmentEvent.ChangeVehicle(vehicles.first { option == it.model })
                )
            },
            hint = "Выберите доступный автомобиль",
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            itemHeight = 56.dp
        ) { optionLabel ->
            val currentOption = vehicles.first {
                it.model == optionLabel
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        text = currentOption.model,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "г/п: ${currentOption.payloadCapacity} кг",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(2f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = currentOption.licensePlate,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}