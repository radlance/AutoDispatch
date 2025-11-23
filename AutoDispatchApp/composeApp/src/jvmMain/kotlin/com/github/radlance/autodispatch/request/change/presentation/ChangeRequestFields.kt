package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.additional_info
import autodispatch.composeapp.generated.resources.cargo_additional_description
import autodispatch.composeapp.generated.resources.cargo_additional_description_placeholder
import autodispatch.composeapp.generated.resources.cargo_info
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.choice_city
import autodispatch.composeapp.generated.resources.choice_type
import autodispatch.composeapp.generated.resources.client_info
import autodispatch.composeapp.generated.resources.company_placeholder
import autodispatch.composeapp.generated.resources.email
import autodispatch.composeapp.generated.resources.from
import autodispatch.composeapp.generated.resources.loading_and_unloading_addresses
import autodispatch.composeapp.generated.resources.loading_point
import autodispatch.composeapp.generated.resources.phone
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.special_req
import autodispatch.composeapp.generated.resources.to
import autodispatch.composeapp.generated.resources.unloading_point
import autodispatch.composeapp.generated.resources.volume_label
import autodispatch.composeapp.generated.resources.weight_label
import com.github.radlance.autodispatch.request.common.presentation.CustomTextField
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.presentation.core.PhoneVisualTransformation
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Customer
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.WeightIcon
import org.jetbrains.compose.resources.stringResource

private enum class PointTarget { LOADING, UNLOADING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRequestFields(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    customers: List<Customer>,
    onEvent: (ChangeRequestEvent) -> Unit,
    fieldsUiState: ChangeRequestFieldsUiState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    var showPointSelectionDialog by rememberSaveable { mutableStateOf(false) }
    var pointSelectionTarget by rememberSaveable { mutableStateOf<PointTarget?>(null) }

    if (showPointSelectionDialog) {
        PointSelectionDialog(
            onDismissRequest = { showPointSelectionDialog = false },
            onConfirm = { selectedPoint ->
                when (pointSelectionTarget) {
                    PointTarget.LOADING -> onEvent(ChangeRequestEvent.ChangeLoading(selectedPoint))
                    PointTarget.UNLOADING -> onEvent(
                        ChangeRequestEvent.ChangeUnloading(
                            selectedPoint
                        )
                    )

                    null -> Unit
                }
                showPointSelectionDialog = false
            }
        )
    }


    Column(modifier = modifier.verticalScroll(scrollState)) {
        Text(text = stringResource(Res.string.route), fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
        Row {
            CustomDropDownMenu(
                label = stringResource(Res.string.from),
                selectedOption = cities.find { fieldsUiState.departureCity == it }?.name,
                filterOptions = cities.map { it.name },
                onOptionSelected = { option ->
                    onEvent(
                        ChangeRequestEvent.ChangeDepartureCity(cities.first { option == it.name })
                    )
                },
                hint = stringResource(Res.string.choice_city),
                modifier = Modifier.weight(1f),
                isRequired = true
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.width(16.dp))

            CustomDropDownMenu(
                label = stringResource(Res.string.to),
                selectedOption = cities.find { fieldsUiState.destinationCity == it }?.name,
                filterOptions = cities.map { it.name },
                onOptionSelected = { option ->
                    onEvent(
                        ChangeRequestEvent.ChangeDestinationCity(cities.first { option == it.name })
                    )
                },
                hint = stringResource(Res.string.choice_city),
                modifier = Modifier.weight(1f),
                isRequired = true
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(text = stringResource(Res.string.client_info), fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
        val recentCompanies = customers.map { it.organizationName }
        CustomTextFieldWithDropdown(
            labelText = stringResource(Res.string.client_info),
            value = fieldsUiState.companyNameFieldValue,
            onValueChange = { value ->
                if ((value != fieldsUiState.companyNameFieldValue)) {
                    onEvent(ChangeRequestEvent.ChangeCompanyName(value))
                }
            },
            placeholder = stringResource(Res.string.company_placeholder),
            suggestions = recentCompanies,
            onSuggestionSelected = { selected ->
                val company = customers.first { it.organizationName == selected }
                onEvent(
                    ChangeRequestEvent.ChangeCompanyName(company.organizationName)
                )
                onEvent(ChangeRequestEvent.ChangeCompanyEmail(company.email))
                onEvent(ChangeRequestEvent.ChangeCompanyPhone(company.phoneNumber.drop(2)))
            },
            modifier = Modifier.fillMaxWidth(),
            isRequired = true,
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                labelText = stringResource(Res.string.email),
                value = fieldsUiState.companyEmailFieldValue,
                onValueChange = { onEvent(ChangeRequestEvent.ChangeCompanyEmail(it)) },
                placeholder = "email@example.com",
                leadingIcon = Icons.Outlined.Mail,
                modifier = Modifier.weight(1f),
                isRequired = true,
                placeholderFontSize = 14.sp,
                searchBarColors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                errorMessage = fieldsUiState.companyEmailErrorMessage
            )
            Spacer(Modifier.width(16.dp))
            CustomTextField(
                labelText = stringResource(Res.string.phone),
                value = fieldsUiState.companyPhoneFieldValue,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    if (digits.length <= 10) {
                        onEvent(ChangeRequestEvent.ChangeCompanyPhone(digits))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = PhoneVisualTransformation(),
                placeholder = "+ 7 (999) 123-45-67",
                leadingIcon = Icons.Outlined.Phone,
                modifier = Modifier.weight(1f),
                isRequired = true,
                placeholderFontSize = 14.sp,
                searchBarColors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                errorMessage = fieldsUiState.companyPhoneErrorMessage
            )
        }

        Spacer(Modifier.height(32.dp))
        Text(text = stringResource(Res.string.cargo_info), fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))

        CustomDropDownMenu(
            label = stringResource(Res.string.cargo_type),
            selectedOption = cargoTypes.find { fieldsUiState.cargoType == it }?.name,
            filterOptions = cargoTypes.map { it.name },
            onOptionSelected = { option ->
                onEvent(ChangeRequestEvent.ChangeCargoType(cargoTypes.first { option == it.name }))
            },
            hint = stringResource(Res.string.choice_type),
            modifier = Modifier.fillMaxWidth(),
            isRequired = true
        ) {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                labelText = stringResource(Res.string.weight_label),
                value = fieldsUiState.cargoWeightFieldValue,
                onValueChange = { onEvent(ChangeRequestEvent.ChangeWeight(it)) },
                placeholder = "100",
                leadingIcon = WeightIcon,
                modifier = Modifier.weight(1f),
                isRequired = true,
                placeholderFontSize = 14.sp,
                searchBarColors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                errorMessage = fieldsUiState.cargoWeightErrorMessage
            )

            Spacer(Modifier.width(16.dp))

            CustomTextField(
                labelText = stringResource(Res.string.volume_label),
                value = fieldsUiState.cargoVolumeFieldValue,
                onValueChange = { onEvent(ChangeRequestEvent.ChangeVolume(it)) },
                placeholder = "2.5",
                leadingIcon = DeployedCodeIcon,
                modifier = Modifier.weight(1f),
                isRequired = false,
                placeholderFontSize = 14.sp,
                searchBarColors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                errorMessage = fieldsUiState.cargoVolumeErrorMessage
            )
        }
        Spacer(Modifier.height(16.dp))
        CustomTextField(
            labelText = stringResource(Res.string.cargo_additional_description),
            value = fieldsUiState.cargoDescriptionFieldValue,
            onValueChange = { onEvent(ChangeRequestEvent.ChangeCargoDescription(it)) },
            placeholder = stringResource(Res.string.cargo_additional_description_placeholder),
            modifier = Modifier.fillMaxWidth(),
            isRequired = false,
            singleLine = false,
            placeholderFontSize = 14.sp,
            searchBarColors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(Res.string.loading_and_unloading_addresses),
            fontSize = 18.sp
        )
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
        Column {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.loading_point))
                    withStyle(SpanStyle(MaterialTheme.colorScheme.error)) { append(" *") }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (fieldsUiState.loadingFieldLatValue == null || fieldsUiState.loadingFieldLonValue == null) {
                OutlinedButton(
                    onClick = {
                        pointSelectionTarget = PointTarget.LOADING
                        showPointSelectionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.NearMe, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Выбрать на карте")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        "Погрузка: ${fieldsUiState.loadingFieldAddressValue}",
                        fontSize = 14.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onEvent(
                                ChangeRequestEvent.ChangeLoading(null)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить точку")
                    }
                }
            }

        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(Res.string.unloading_point))
                withStyle(SpanStyle(MaterialTheme.colorScheme.error)) { append(" *") }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (fieldsUiState.unloadingFieldLatValue == null || fieldsUiState.unloadingFieldLonValue == null) {
            OutlinedButton(
                onClick = {
                    pointSelectionTarget = PointTarget.UNLOADING
                    showPointSelectionDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.NearMe, null)
                Spacer(Modifier.width(12.dp))
                Text("Выбрать на карте")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    "Выгрузка: ${fieldsUiState.unloadingFieldAddressValue}",
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onEvent(
                            ChangeRequestEvent.ChangeUnloading(null)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить точку")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        CustomTextField(
            labelText = stringResource(Res.string.additional_info),
            value = fieldsUiState.additionalInfoFieldValue,
            onValueChange = { onEvent(ChangeRequestEvent.ChangeAdditionalInfo(it)) },
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
}