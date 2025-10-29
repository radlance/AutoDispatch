package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.WeightIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCreationDialog(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedDepartureCityId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedDestinationCityId by rememberSaveable { mutableStateOf<Int?>(null) }
    var companyName by rememberSaveable { mutableStateOf("") }
    var companyEmail by rememberSaveable { mutableStateOf("") }
    var companyPhone by rememberSaveable { mutableStateOf("") }
    var selectedCargoTypeId by rememberSaveable { mutableStateOf<Int?>(null) }
    var cargoWeight by rememberSaveable { mutableStateOf("") }
    var cargoVolume by rememberSaveable { mutableStateOf("") }
    var loadingPoint by rememberSaveable { mutableStateOf("") }
    var unloadingPoint by rememberSaveable { mutableStateOf("") }
    var additionalInfo by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Создание новой заявки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(text = "Маршрут", fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                    Row {
                        CustomDropDownMenu(
                            label = "Откуда",
                            selectedOption = cities.find { selectedDepartureCityId == it.id }?.name,
                            filterOptions = cities.map { it.name },
                            onOptionSelected = { option ->
                                selectedDepartureCityId = cities.first { option == it.name }.id
                            },
                            hint = "Выберите город",
                            modifier = Modifier.weight(1f),
                            isRequired = true
                        )

                        Spacer(Modifier.width(16.dp))

                        CustomDropDownMenu(
                            label = "Куда",
                            selectedOption = cities.find { selectedDestinationCityId == it.id }?.name,
                            filterOptions = cities.map { it.name },
                            onOptionSelected = { option ->
                                selectedDestinationCityId = cities.first { option == it.name }.id
                            },
                            hint = "Выберите город",
                            modifier = Modifier.weight(1f),
                            isRequired = true
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(text = "Информация о клиенте", fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))

                    CustomTextField(
                        labelText = "Название/ФИО",
                        value = companyName,
                        onValueChange = { companyName = it },
                        placeholder = "ООО \"Компания\"",
                        leadingIcon = Icons.Outlined.Person,
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = true,
                        placeholderFontSize = 14.sp,
                        searchBarColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CustomTextField(
                            labelText = "Email",
                            value = companyEmail,
                            onValueChange = { companyEmail = it },
                            placeholder = "email@example.com",
                            leadingIcon = Icons.Outlined.Mail,
                            modifier = Modifier.weight(1f),
                            isRequired = true,
                            placeholderFontSize = 14.sp,
                            searchBarColors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Spacer(Modifier.width(16.dp))
                        CustomTextField(
                            labelText = "Телефон",
                            value = companyPhone,
                            onValueChange = { companyPhone = it },
                            placeholder = "+ 7 (999) 123-45-67",
                            leadingIcon = Icons.Outlined.Phone,
                            modifier = Modifier.weight(1f),
                            isRequired = false,
                            placeholderFontSize = 14.sp,
                            searchBarColors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(text = "Информация о грузе", fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))

                    CustomDropDownMenu(
                        label = "Тип груза",
                        selectedOption = cargoTypes.find { selectedCargoTypeId == it.id }?.name,
                        filterOptions = cargoTypes.map { it.name },
                        onOptionSelected = { option ->
                            selectedCargoTypeId = cargoTypes.first { option == it.name }.id
                        },
                        hint = "Выберите тип",
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = true
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CustomTextField(
                            labelText = "Вес (кг)",
                            value = cargoWeight,
                            onValueChange = {
                                cargoWeight = it
                            },
                            placeholder = "100",
                            leadingIcon = WeightIcon,
                            modifier = Modifier.weight(1f),
                            isRequired = true,
                            placeholderFontSize = 14.sp,
                            searchBarColors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        Spacer(Modifier.width(16.dp))

                        CustomTextField(
                            labelText = "Объём (м\u00B3)",
                            value = cargoVolume,
                            onValueChange = {
                                cargoVolume = it
                            },
                            placeholder = "2.5",
                            leadingIcon = DeployedCodeIcon,
                            modifier = Modifier.weight(1f),
                            isRequired = false,
                            placeholderFontSize = 14.sp,
                            searchBarColors = SearchBarDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(text = "Адреса погрузки и выгрузки", fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                    CustomTextField(
                        labelText = "Точка погрузки",
                        value = loadingPoint,
                        onValueChange = {
                            loadingPoint = it
                        },
                        placeholder = "Склад, Москва, ул. Промышленная 5",
                        leadingIcon = Icons.Outlined.LocationOn,
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = true,
                        placeholderFontSize = 14.sp,
                        searchBarColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(
                        labelText = "Точка выгрузки",
                        value = unloadingPoint,
                        onValueChange = {
                            unloadingPoint = it
                        },
                        placeholder = "Склад получателя, СПБ, пр. Обуховской обороны 120",
                        leadingIcon = Icons.Outlined.LocationOn,
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = true,
                        placeholderFontSize = 14.sp,
                        searchBarColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(
                        labelText = "Дополнительная информация",
                        value = additionalInfo,
                        onValueChange = {
                            additionalInfo = it
                        },
                        placeholder = "Особые требования к перевозке, условия загрузки/разгрузки…",
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = false,
                        singleLine = false,
                        placeholderFontSize = 14.sp,
                        searchBarColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .offset(x = 10.dp)
                )
            }
        },
        confirmButton = {

        },
        dismissButton = {

        },
        shape = RoundedCornerShape(16.dp)
    )
}