package com.github.radlance.autodispatch.request.core.presentation

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
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.extended_search
import autodispatch.composeapp.generated.resources.from
import autodispatch.composeapp.generated.resources.status
import autodispatch.composeapp.generated.resources.to
import autodispatch.composeapp.generated.resources.vehicle
import com.github.radlance.autodispatch.common.domain.RequestStatus
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.UserFilter
import com.github.radlance.autodispatch.request.core.domain.Vehicle
import org.jetbrains.compose.resources.stringResource

@Composable
fun RequestFilters(
    selectedDepartureCities: List<String>,
    selectedDestinationCities: List<String>,
    selectedCargoTypes: List<String>,
    selectedStatuses: List<String>,
    selectedDrivers: List<String>,
    selectedVehicles: List<String>,
    cities: List<City>,
    filterCargoTypes: List<CargoType>,
    filterStatuses: List<RequestStatus>,
    filterDrivers: List<UserFilter>,
    filterVehicles: List<Vehicle>,
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
                    options = cities.map { it.name },
                    selected = selectedDepartureCities,
                    onSelectionChanged = onDepartureCitiesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.to),
                    options = cities.map { it.name },
                    selected = selectedDestinationCities,
                    onSelectionChanged = onDestinationCitiesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.cargo_type),
                    options = filterCargoTypes.map { it.name },
                    selected = selectedCargoTypes,
                    onSelectionChanged = onCargoTypesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.status),
                    options = filterStatuses.map { it.title },
                    selected = selectedStatuses,
                    onSelectionChanged = onStatusesChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.driver),
                    options = filterDrivers.map { it.fullName },
                    selected = selectedDrivers,
                    onSelectionChanged = onDriversChanged
                )
                FilterDialogSelector(
                    title = stringResource(Res.string.vehicle),
                    options = filterVehicles.map { it.model },
                    selected = selectedVehicles,
                    onSelectionChanged = onVehiclesChanged
                )
            }
        }
    }
}