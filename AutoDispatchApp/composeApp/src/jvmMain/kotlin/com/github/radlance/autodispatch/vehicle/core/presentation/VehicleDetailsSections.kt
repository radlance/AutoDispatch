package com.github.radlance.autodispatch.vehicle.core.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.status
import com.github.radlance.autodispatch.common.presentation.ITEM_GAP
import com.github.radlance.autodispatch.common.presentation.LabeledValue
import com.github.radlance.autodispatch.common.presentation.SECTION_GAP
import com.github.radlance.autodispatch.common.presentation.Section
import com.github.radlance.autodispatch.vehicle.core.domain.VehicleDetailed
import org.jetbrains.compose.resources.stringResource

@Composable
fun VehicleDetailsSections(
    scrollState: ScrollState,
    vehicle: VehicleDetailed,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(SECTION_GAP))
        Section(header = stringResource(Res.string.status)) {
            VehicleStatusWithColor(
                isAvailable = vehicle.driverFullName != null,
                fontSize = 14.sp
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )

        Section(header = "Автомобиль") {
            LabeledValue(label = "Модель", value = vehicle.model)
            Spacer(modifier = Modifier.height(ITEM_GAP))
            LabeledValue(label = "Гос. номер", value = vehicle.licensePlate)
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
        )
        Section(header = "Водитель") {
            LabeledValue(label = "ФИО", value = vehicle.driverFullName)
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (vehicle.driverFullName == null) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
            ) {
                Text(text = "Назначить водителя")
            }
        }
    }
}