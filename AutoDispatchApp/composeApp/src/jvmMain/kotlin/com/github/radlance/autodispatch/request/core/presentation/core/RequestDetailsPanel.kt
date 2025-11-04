package com.github.radlance.autodispatch.request.core.presentation.core

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.additional_info
import autodispatch.composeapp.generated.resources.vehicle
import autodispatch.composeapp.generated.resources.cargo_info
import autodispatch.composeapp.generated.resources.cargo_type
import autodispatch.composeapp.generated.resources.creation_date
import autodispatch.composeapp.generated.resources.customer
import autodispatch.composeapp.generated.resources.customer_email
import autodispatch.composeapp.generated.resources.customer_phone
import autodispatch.composeapp.generated.resources.description
import autodispatch.composeapp.generated.resources.driver
import autodispatch.composeapp.generated.resources.driver_and_vehicle
import autodispatch.composeapp.generated.resources.loading_point
import autodispatch.composeapp.generated.resources.loading_unloading_points
import autodispatch.composeapp.generated.resources.request_creation_date
import autodispatch.composeapp.generated.resources.route
import autodispatch.composeapp.generated.resources.status
import autodispatch.composeapp.generated.resources.unloading_point
import autodispatch.composeapp.generated.resources.volume
import autodispatch.composeapp.generated.resources.weight
import com.github.radlance.autodispatch.request.assignment.presentation.DriverAssignmentDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestFieldsUiState
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Request
import org.jetbrains.compose.resources.stringResource

private val SECTION_GAP = 18.dp
private val ITEM_GAP = 12.dp
private val ICON_TEXT_GAP = 6.dp

@Composable
fun RequestDetailsPanel(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    onClosePanel: () -> Unit,
    onSuccessCreateRequest: () -> Unit,
    request: Request,
    modifier: Modifier = Modifier
) {
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showEditDialog) {
        with(request) {
            ChangeRequestDialog(
                cities = cities,
                cargoTypes = cargoTypes,
                onDismiss = { showEditDialog = false },
                onSuccessCreateRequest = onSuccessCreateRequest,
                isEditRequest = true,
                currentFieldsUiState = ChangeRequestFieldsUiState(
                    departureCity = cities.first { it.name == request.origin },
                    destinationCity = cities.first { it.name == request.destination },
                    cargoType = cargoTypes.first { it.name == request.cargoTypeName },
                    requestNumber = request.requestNumber,
                    companyNameFieldValue = organizationName,
                    companyEmailFieldValue = organizationEmail,
                    companyPhoneFieldValue = organizationPhoneNumber?.removePrefix("+7") ?: "",
                    cargoWeightFieldValue = cargoWeight.formatNumberNoTrailingZeros(),
                    cargoVolumeFieldValue = cargoVolume?.formatNumberNoTrailingZeros() ?: "",
                    cargoDescriptionFieldValue = cargoDescription ?: "",
                    loadingFieldValue = loadingPoint,
                    unloadingFieldValue = unloadingPoint,
                    additionalInfoFieldValue = transportationDescription ?: "",
                    requestId = request.id
                )
            )
        }
    }

    if (showDriverAssignmentDialog) {
        DriverAssignmentDialog(
            onDismiss = { showDriverAssignmentDialog = false },
            request = request
        )
    }

    Column(modifier = modifier.padding(8.dp)) {
        PanelHeader(
            requestNumber = request.requestNumber,
            onSettingsClick = { showEditDialog = true },
            onClose = onClosePanel
        )

        Box {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Spacer(modifier = Modifier.height(SECTION_GAP))

                Section(header = stringResource(Res.string.status)) {
                    StatusWithColor(
                        status = request.status.name,
                        fontSize = 14.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.route)) {
                    InfoRow(
                        icon = Icons.Outlined.LocationOn,
                        iconDesc = stringResource(Res.string.route),
                        text = routeText(request)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.request_creation_date)) {
                    InfoRow(
                        icon = Icons.Outlined.CalendarToday,
                        iconDesc = stringResource(Res.string.creation_date),
                        text = "${request.createdAt.date}, ${request.createdAt.hour}:${request.createdAt.minute}:${request.createdAt.second}"
                    )
                }

                Spacer(modifier = Modifier.height(SECTION_GAP))

                Section(header = stringResource(Res.string.cargo_info)) {
                    LabeledValue(
                        label = stringResource(Res.string.cargo_type),
                        value = request.cargoTypeName
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.weight),
                        value = request.cargoWeight.formatKg()
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.volume),
                        value = request.cargoVolume?.formatM3()
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.description),
                        value = request.cargoDescription
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.loading_unloading_points)) {
                    LabeledValue(
                        label = stringResource(Res.string.loading_point),
                        value = request.loadingPoint
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.unloading_point),
                        value = request.unloadingPoint
                    )
                    request.transportationDescription?.let {
                        Spacer(modifier = Modifier.height(ITEM_GAP))
                        LabeledValue(
                            label = stringResource(Res.string.additional_info),
                            value = request.transportationDescription
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.customer)) {
                    InfoRow(
                        icon = Icons.Outlined.Person,
                        iconDesc = stringResource(Res.string.customer),
                        text = request.organizationName
                    )

                    request.organizationPhoneNumber?.let {
                        Spacer(modifier = Modifier.height(ITEM_GAP))
                        InfoRow(
                            icon = Icons.Outlined.Phone,
                            iconDesc = stringResource(Res.string.customer_phone),
                            text = it
                        )
                    }

                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    InfoRow(
                        icon = Icons.Outlined.Mail,
                        iconDesc = stringResource(Res.string.customer_email),
                        text = request.organizationEmail
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.driver_and_vehicle)) {
                    LabeledValue(
                        label = stringResource(Res.string.driver),
                        value = request.driverFullName
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    val vehicle = request.vehicleInfo?.takeIf { it.isNotBlank() } ?: "—"
                    LabeledValue(label = stringResource(Res.string.vehicle), value = vehicle)
                }

                Spacer(modifier = Modifier.height(12.dp))
                if (request.status.id == 1) {
                    Button(
                        onClick = { showDriverAssignmentDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
                    ) {
                        Text(text = "Назначить водителя")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}

@Composable
private fun Section(
    header: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = header,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(ITEM_GAP))
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconDesc: String,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = iconDesc,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(ICON_TEXT_GAP))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LabeledValue(label: String, value: String?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp
        )
    }
}

private fun routeText(request: Request): String =
    listOf(request.origin, request.destination)
        .filter { it.isNotBlank() }
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" → ")
        ?: "—"

private fun Double.formatNumberNoTrailingZeros(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        var s = this.toString()
        if (s.contains('.')) {
            s = s.trimEnd('0').trimEnd('.')
        }
        s
    }
}

private fun Double?.formatKg(): String =
    this?.let { "${it.formatNumberNoTrailingZeros()} кг" } ?: "—"

private fun Double?.formatM3(): String =
    this?.let { "${it.formatNumberNoTrailingZeros()} м\u00B3" } ?: "—"
