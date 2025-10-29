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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.additional_info
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.cargo_info
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.choice_city
import autodispatch.composeapp.generated.resources.choice_type
import autodispatch.composeapp.generated.resources.client_info
import autodispatch.composeapp.generated.resources.company_placeholder
import autodispatch.composeapp.generated.resources.create_request
import autodispatch.composeapp.generated.resources.creating_new_request
import autodispatch.composeapp.generated.resources.email
import autodispatch.composeapp.generated.resources.from
import autodispatch.composeapp.generated.resources.loading_and_unloading_addresses
import autodispatch.composeapp.generated.resources.loading_point
import autodispatch.composeapp.generated.resources.loading_point_placeholder
import autodispatch.composeapp.generated.resources.phone
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.special_req
import autodispatch.composeapp.generated.resources.to
import autodispatch.composeapp.generated.resources.unloading_point
import autodispatch.composeapp.generated.resources.unloading_point_placeholder
import autodispatch.composeapp.generated.resources.volume_label
import autodispatch.composeapp.generated.resources.weight_label
import com.github.radlance.autodispatch.request.domain.CargoType
import com.github.radlance.autodispatch.request.domain.City
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.WeightIcon
import org.jetbrains.compose.resources.stringResource

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
                stringResource(Res.string.creating_new_request),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(text = stringResource(Res.string.route), fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                    Row {
                        CustomDropDownMenu(
                            label = stringResource(Res.string.from),
                            selectedOption = cities.find { selectedDepartureCityId == it.id }?.name,
                            filterOptions = cities.map { it.name },
                            onOptionSelected = { option ->
                                selectedDepartureCityId = cities.first { option == it.name }.id
                            },
                            hint = stringResource(Res.string.choice_city),
                            modifier = Modifier.weight(1f),
                            isRequired = true
                        )

                        Spacer(Modifier.width(16.dp))

                        CustomDropDownMenu(
                            label = stringResource(Res.string.to),
                            selectedOption = cities.find { selectedDestinationCityId == it.id }?.name,
                            filterOptions = cities.map { it.name },
                            onOptionSelected = { option ->
                                selectedDestinationCityId = cities.first { option == it.name }.id
                            },
                            hint = stringResource(Res.string.choice_city),
                            modifier = Modifier.weight(1f),
                            isRequired = true
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(text = stringResource(Res.string.client_info), fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))

                    CustomTextField(
                        labelText = stringResource(Res.string.client_info),
                        value = companyName,
                        onValueChange = { companyName = it },
                        placeholder = stringResource(Res.string.company_placeholder),
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
                            labelText = stringResource(Res.string.email),
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
                            labelText = stringResource(Res.string.phone),
                            value = companyPhone,
                            onValueChange = { newValue ->
                                val digits = newValue.filter { it.isDigit() }
                                if (digits.length <= 10) {
                                    companyPhone = digits
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            visualTransformation = PhoneVisualTransformation(),
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
                    Text(text = stringResource(Res.string.cargo_info), fontSize = 18.sp)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))

                    CustomDropDownMenu(
                        label = stringResource(Res.string.cargo_type),
                        selectedOption = cargoTypes.find { selectedCargoTypeId == it.id }?.name,
                        filterOptions = cargoTypes.map { it.name },
                        onOptionSelected = { option ->
                            selectedCargoTypeId = cargoTypes.first { option == it.name }.id
                        },
                        hint = stringResource(Res.string.choice_type),
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = true
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CustomTextField(
                            labelText = stringResource(Res.string.weight_label),
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
                            labelText = stringResource(Res.string.volume_label),
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
                    Text(
                        text = stringResource(Res.string.loading_and_unloading_addresses),
                        fontSize = 18.sp
                    )
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                    CustomTextField(
                        labelText = stringResource(Res.string.loading_point),
                        value = loadingPoint,
                        onValueChange = {
                            loadingPoint = it
                        },
                        placeholder = stringResource(Res.string.loading_point_placeholder),
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
                        labelText = stringResource(Res.string.unloading_point),
                        value = unloadingPoint,
                        onValueChange = {
                            unloadingPoint = it
                        },
                        placeholder = stringResource(Res.string.unloading_point_placeholder),
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
                        labelText = stringResource(Res.string.additional_info),
                        value = additionalInfo,
                        onValueChange = {
                            additionalInfo = it
                        },
                        placeholder = stringResource(Res.string.special_req),
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
            Button(onClick = {}) {
                Text(text = stringResource(Res.string.create_request))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}