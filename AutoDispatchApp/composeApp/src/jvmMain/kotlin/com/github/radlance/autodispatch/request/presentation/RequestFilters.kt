package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RequestFilters(
    selectedDepartureCity: String,
    selectedDestinationCity: String,
    selectedCargoType: String,
    selectedStatus: String,
    selectedDriver: String,
    selectedVehicle: String,
    filterDepartureCities: List<String>,
    filterDestinationCities: List<String>,
    filterCargoTypes: List<String>,
    filterStatuses: List<String>,
    filterDrivers: List<String>,
    filterVehicles: List<String>,
    onDepartureCitySelected: (String) -> Unit,
    onDestinationCitySelected: (String) -> Unit,
    onCargoTypeSelected: (String) -> Unit,
    onStatusSelected: (String) -> Unit,
    onDriverSelected: (String) -> Unit,
    onVehicleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(text = "Расширенный поиск")
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CustomDropDownMenu(
                        label = "Город отправки",
                        selectedOption = selectedDepartureCity,
                        filterOptions = filterDepartureCities,
                        onOptionSelected = onDepartureCitySelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                    CustomDropDownMenu(
                        label = "Город назначения",
                        selectedOption = selectedDestinationCity,
                        filterOptions = filterDestinationCities,
                        onOptionSelected = onDestinationCitySelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                    CustomDropDownMenu(
                        label = "Тип груза",
                        selectedOption = selectedCargoType,
                        filterOptions = filterCargoTypes,
                        onOptionSelected = onCargoTypeSelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                    CustomDropDownMenu(
                        label = "Статус",
                        selectedOption = selectedStatus,
                        filterOptions = filterStatuses,
                        onOptionSelected = onStatusSelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                    CustomDropDownMenu(
                        label = "Водитель",
                        selectedOption = selectedDriver,
                        filterOptions = filterDrivers,
                        onOptionSelected = onDriverSelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                    CustomDropDownMenu(
                        label = "Автомобиль",
                        selectedOption = selectedVehicle,
                        filterOptions = filterVehicles,
                        onOptionSelected = onVehicleSelected,
                        modifier = Modifier.weight(1f).widthIn(min = 200.dp)
                    )
                }
            }
        }
    }
}
