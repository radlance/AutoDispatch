package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.SimpleCustomDialog
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Customer
import com.github.radlance.autodispatch.request.core.presentation.PhoneVisualTransformation
import com.github.radlance.autodispatch.uikit.vector.DeployedCodeIcon
import com.github.radlance.autodispatch.uikit.vector.WeightIcon
import org.jetbrains.compose.resources.stringResource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private enum class PointTarget { LOADING, UNLOADING }
private enum class DateTarget { LOADING, UNLOADING }

enum class ChangeRequestFieldAnchor {
    ROUTE,
    PLANNED_DATE_TIME,
    CLIENT_INFO,
    CARGO_INFO,
    LOADING_POINT,
    UNLOADING_POINT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRequestFields(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    customers: List<Customer>,
    onEvent: (ChangeRequestEvent) -> Unit,
    fieldsUiState: ChangeRequestFieldsUiState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onAnchorPositioned: (ChangeRequestFieldAnchor, Int) -> Unit = { _, _ -> }
) {
    var showPointSelectionDialog by rememberSaveable { mutableStateOf(false) }
    var pointSelectionTarget by rememberSaveable { mutableStateOf<PointTarget?>(null) }
    var selectedCity by rememberSaveable { mutableStateOf<City?>(null) }
    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var datePickerTarget by rememberSaveable { mutableStateOf<DateTarget?>(null) }
    var selectedDateForTimePicker by rememberSaveable { mutableStateOf<String?>(null) }

    if (showPointSelectionDialog) {
        PointSelectionDialog(
            selectedCityName = selectedCity!!.name,
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

    if (showDatePickerDialog) {

        val isLoadingDate = datePickerTarget == DateTarget.LOADING

        val loadValue = fieldsUiState.plannedLoadingAt
        val unloadValue = fieldsUiState.plannedUnloadingAt

        PlannedDatePickerDialog(
            title = if (isLoadingDate)
                "Ожидаемая дата загрузки"
            else
                "Ожидаемая дата разгрузки",

            initialDateValue = if (isLoadingDate) loadValue else unloadValue,

            maxDateValue = if (isLoadingDate && unloadValue.isNotBlank())
                unloadValue else null,

            minDateValue = if (!isLoadingDate && loadValue.isNotBlank())
                loadValue else null,

            onDismissRequest = {
                showDatePickerDialog = false
                datePickerTarget = null
            },

            onConfirm = { value ->
                selectedDateForTimePicker = value
                showDatePickerDialog = false
                showTimePickerDialog = true
            }
        )
    }

    if (showTimePickerDialog) {

        val isLoadingDate = datePickerTarget == DateTarget.LOADING
        val currentValue = if (isLoadingDate)
            fieldsUiState.plannedLoadingAt
        else
            fieldsUiState.plannedUnloadingAt

        PlannedTimePickerDialog(
            title = if (isLoadingDate)
                "Ожидаемое время загрузки"
            else
                "Ожидаемое время разгрузки",

            initialDateTimeValue = currentValue,

            onDismissRequest = {
                showTimePickerDialog = false
                selectedDateForTimePicker = null
                datePickerTarget = null
            },

            onConfirm = { hour, minute ->

                val selectedDate = selectedDateForTimePicker!!
                    .let { LocalDate.parse(it) }

                val value = toServerOffsetDateTimeValue(selectedDate, hour, minute)

                when (datePickerTarget) {
                    DateTarget.LOADING ->
                        onEvent(ChangeRequestEvent.ChangePlannedLoadingAt(value))

                    DateTarget.UNLOADING ->
                        onEvent(ChangeRequestEvent.ChangePlannedUnloadingAt(value))

                    null -> Unit
                }

                selectedDateForTimePicker = null
                showTimePickerDialog = false
                datePickerTarget = null
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(text = stringResource(Res.string.route), fontSize = 18.sp)
        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
        Row(
            modifier = Modifier.onGloballyPositioned {
                onAnchorPositioned(
                    ChangeRequestFieldAnchor.ROUTE,
                    it.positionInParent().y.toInt()
                )
            }
        ) {
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
                isRequired = true,
                showErrorBorder = fieldsUiState.departureCityError
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
                isRequired = true,
                showErrorBorder = fieldsUiState.destinationCityError
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.onGloballyPositioned {
                onAnchorPositioned(
                    ChangeRequestFieldAnchor.PLANNED_DATE_TIME,
                    it.positionInParent().y.toInt()
                )
            }
        ) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = {
                        datePickerTarget = DateTarget.LOADING
                        showDatePickerDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (fieldsUiState.plannedLoadingAt.isBlank()) {
                            "Ожидаемая дата загрузки"
                        } else {
                            "Загр.: ${formatPlannedDateTimeLabel(fieldsUiState.plannedLoadingAt)}"
                        },
                        fontSize = if (fieldsUiState.plannedLoadingAt.isBlank()) 12.sp else 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = {
                        datePickerTarget = DateTarget.UNLOADING
                        showDatePickerDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (fieldsUiState.plannedUnloadingAt.isBlank()) {
                            "Ожидаемая дата разгрузки"
                        } else {
                            "Разгр.: ${formatPlannedDateTimeLabel(fieldsUiState.plannedUnloadingAt)}"
                        },
                        fontSize = if (fieldsUiState.plannedUnloadingAt.isBlank()) 12.sp else 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
            isRequired = true,
            leadingIcon = Icons.Outlined.Person,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    onAnchorPositioned(
                        ChangeRequestFieldAnchor.CLIENT_INFO,
                        it.positionInParent().y.toInt()
                    )
                },
            errorMessage = if (fieldsUiState.companyNameError) "Введите название компании" else ""
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
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    onAnchorPositioned(
                        ChangeRequestFieldAnchor.CARGO_INFO,
                        it.positionInParent().y.toInt()
                    )
                },
            isRequired = true,
            showErrorBorder = fieldsUiState.cargoTypeError
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
        Column(
            modifier = Modifier.onGloballyPositioned {
                onAnchorPositioned(
                    ChangeRequestFieldAnchor.LOADING_POINT,
                    it.positionInParent().y.toInt()
                )
            }
        ) {
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
                        selectedCity = fieldsUiState.departureCity
                        showPointSelectionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fieldsUiState.departureCity != null,
                    border = if (fieldsUiState.loadingPointError && fieldsUiState.departureCity != null) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.outlinedButtonBorder(true)
                    }
                ) {
                    Icon(Icons.Outlined.NearMe, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Выбрать на карте")
                }
                if (fieldsUiState.loadingPointError && fieldsUiState.departureCity != null) {
                    Text(
                        text = "Выберите точку погрузки",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
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
        Column(
            modifier = Modifier.onGloballyPositioned {
                onAnchorPositioned(
                    ChangeRequestFieldAnchor.UNLOADING_POINT,
                    it.positionInParent().y.toInt()
                )
            }
        ) {
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
                        selectedCity = fieldsUiState.destinationCity
                        showPointSelectionDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = fieldsUiState.destinationCity != null,
                    border = if (fieldsUiState.unloadingPointError && fieldsUiState.destinationCity != null) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.outlinedButtonBorder(true)
                    }
                ) {
                    Icon(Icons.Outlined.NearMe, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Выбрать на карте")
                }
                if (fieldsUiState.unloadingPointError && fieldsUiState.destinationCity != null) {
                    Text(
                        text = "Выберите точку выгрузки",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlannedDatePickerDialog(
    title: String,
    initialDateValue: String,
    minDateValue: String? = null,
    maxDateValue: String? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialDate = rememberDatePickerMillis(initialDateValue)
    val minDateMillis = minDateValue?.let { rememberDatePickerMillis(it) }
        ?: LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    val maxDateMillis = maxDateValue?.let { rememberDatePickerMillis(it) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val isAfterMin = utcTimeMillis >= minDateMillis
                val isBeforeMax =
                    if (maxDateMillis != null) utcTimeMillis <= maxDateMillis else true
                return isAfterMin && isBeforeMax
            }
        }
    )

    SimpleCustomDialog(onDismissRequest = onDismissRequest) { requestDismiss ->
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AlertDialogDefaults.containerColor
        ) {
            Column(
                modifier = Modifier
                    .width(420.dp)
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = requestDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedMillis ->
                                onConfirm(epochMillisToIsoDate(selectedMillis))
                            }
                            requestDismiss()
                        },
                        enabled = datePickerState.selectedDateMillis != null
                    ) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlannedTimePickerDialog(
    title: String,
    initialDateTimeValue: String,
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val initialDateTime = rememberParsedDateTime(initialDateTimeValue)

    val timePickerState = rememberTimePickerState(
        initialHour = initialDateTime?.hour ?: 9,
        initialMinute = initialDateTime?.minute ?: 0,
        is24Hour = true
    )

    SimpleCustomDialog(onDismissRequest = onDismissRequest) { requestDismiss ->
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AlertDialogDefaults.containerColor
        ) {
            Column(
                modifier = Modifier
                    .width(420.dp)
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(
                        state = timePickerState,
                        layoutType = TimePickerLayoutType.Vertical
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Spacer(Modifier.weight(1f))

                    TextButton(onClick = requestDismiss) {
                        Text("Отмена")
                    }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            requestDismiss()
                        }
                    ) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}
private fun rememberDatePickerMillis(rawValue: String): Long? {
    val date = rememberParsedDateTime(rawValue)?.toLocalDate()
        ?: rawValue.takeIf { it.isNotBlank() }
            ?.take(10)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null

    return date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
}

private fun epochMillisToIsoDate(epochMillis: Long): String {
    return Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate().toString()
}

private fun rememberParsedDateTime(rawValue: String): LocalDateTime? {
    if (rawValue.isBlank()) return null

    return runCatching { OffsetDateTime.parse(rawValue).toLocalDateTime() }.getOrNull()
        ?: runCatching { LocalDateTime.parse(rawValue) }.getOrNull()
        ?: runCatching { LocalDate.parse(rawValue.take(10)).atTime(9, 0) }.getOrNull()
}

private fun formatPlannedDateTimeLabel(rawValue: String): String {
    val parsed = rememberParsedDateTime(rawValue)
        ?: return rawValue

    return parsed.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"))
}

private fun toServerOffsetDateTimeValue(
    date: LocalDate,
    hour: Int,
    minute: Int
): String {
    val localDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute))
    val offset = ZoneId.systemDefault().rules.getOffset(localDateTime)
    return OffsetDateTime.of(localDateTime, offset)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
