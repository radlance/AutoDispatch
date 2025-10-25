package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.car
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.extended_search
import autodispatch.composeapp.generated.resources.from
import autodispatch.composeapp.generated.resources.status
import autodispatch.composeapp.generated.resources.to
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestFilters(
    selectedDepartureCities: List<String>,
    selectedDestinationCities: List<String>,
    selectedCargoTypes: List<String>,
    selectedStatuses: List<String>,
    selectedDrivers: List<String>,
    selectedVehicles: List<String>,
    filterDepartureCities: List<String>,
    filterDestinationCities: List<String>,
    filterCargoTypes: List<String>,
    filterStatuses: List<String>,
    filterDrivers: List<String>,
    filterVehicles: List<String>,
    onDepartureCitiesChanged: (List<String>) -> Unit,
    onDestinationCitiesChanged: (List<String>) -> Unit,
    onCargoTypesChanged: (List<String>) -> Unit,
    onStatusesChanged: (List<String>) -> Unit,
    onDriversChanged: (List<String>) -> Unit,
    onVehiclesChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(Res.string.extended_search),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterDialogSelector(
                    title = stringResource(Res.string.from),
                    options = filterDepartureCities,
                    selected = selectedDepartureCities,
                    onSelectionChanged = onDepartureCitiesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.to),
                    options = filterDestinationCities,
                    selected = selectedDestinationCities,
                    onSelectionChanged = onDestinationCitiesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.cargo_type),
                    options = filterCargoTypes,
                    selected = selectedCargoTypes,
                    onSelectionChanged = onCargoTypesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.status),
                    options = filterStatuses,
                    selected = selectedStatuses,
                    onSelectionChanged = onStatusesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.driver),
                    options = filterDrivers,
                    selected = selectedDrivers,
                    onSelectionChanged = onDriversChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.car),
                    options = filterVehicles,
                    selected = selectedVehicles,
                    onSelectionChanged = onVehiclesChanged
                )
            }
        }
    }
}