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
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.additional_info
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
import autodispatch.composeapp.generated.resources.vehicle
import autodispatch.composeapp.generated.resources.volume
import autodispatch.composeapp.generated.resources.weight
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.presentation.StatusWithColor
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.common.utils.formatM3
import com.github.radlance.autodispatch.common.utils.formatNumberNoTrailingZeros
import com.github.radlance.autodispatch.common.utils.toStringAddress
import com.github.radlance.autodispatch.request.assignment.presentation.DriverAssignmentDialog
import com.github.radlance.autodispatch.request.change.presentation.CancelDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestEvent
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestFieldsUiState
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestViewModel
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.reuqest.core.domain.CargoType
import com.github.radlance.autodispatch.reuqest.core.domain.Request
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
    modifier: Modifier = Modifier,
    viewModel: ChangeRequestViewModel = koinViewModel()
) {
    val cancelRequestState by viewModel.cancelRequestState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(false) }
    var showCancelAssignmentDialog by remember { mutableStateOf(false) }
    var showReassignErrorDialog by remember { mutableStateOf(false) }
    var reassignErrorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    if (showReassignErrorDialog) {
        val onDismiss: () -> Unit = {
            showReassignErrorDialog = false
            onSuccessCreateRequest()
            scope.launch {
                scrollState.animateScrollTo(0)
            }
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Ошибка")
            },
            text = {
                Text(text = reassignErrorMessage)
            },
            dismissButton = {},
            confirmButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(text = "ОК")
                }
            }
        )
    }

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
                    cargoType = cargoTypes.first { it.name == request.cargo.type.name },
                    requestNumber = request.requestNumber,
                    companyNameFieldValue = customer.organizationName,
                    companyEmailFieldValue = customer.email,
                    companyPhoneFieldValue = customer.phoneNumber.removePrefix("+7"),
                    cargoWeightFieldValue = cargo.weight.formatNumberNoTrailingZeros(),
                    cargoVolumeFieldValue = cargo.volume?.formatNumberNoTrailingZeros() ?: "",
                    cargoDescriptionFieldValue = cargo.description ?: "",
                    loadingFieldAddressValue = loadingPoint.address ?: "",
                    loadingFieldLonValue = loadingPoint.lon,
                    loadingFieldLatValue = loadingPoint.lat,
                    unloadingFieldAddressValue = unloadingPoint.address ?: "",
                    unloadingFieldLonValue = unloadingPoint.lon,
                    unloadingFieldLatValue = unloadingPoint.lat,
                    additionalInfoFieldValue = transportationDescription ?: "",
                    requestId = request.id
                )
            )
        }
    }

    if (showDriverAssignmentDialog) {
        DriverAssignmentDialog(
            onDismiss = { showDriverAssignmentDialog = false },
            onSuccessAssignRequest = {
                onSuccessCreateRequest()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            },
            request = request,
            isReassign = isReassign,
            assignedDriverId = request.driverId,
            onStateReassignError = {
                showDriverAssignmentDialog = false
                showReassignErrorDialog = true
                reassignErrorMessage = it
            }
        )
    }

    if (showCancelAssignmentDialog) {
        val onDismissCancelDialog = {
            showCancelAssignmentDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetCancelState)
        }

        LaunchedEffect(cancelRequestState) {
            if (cancelRequestState is FetchResultUiState.Success) {
                onSuccessCreateRequest()
                viewModel.reduce(ChangeRequestEvent.ResetChangeState)
                onDismissCancelDialog()
            }
        }

        CancelDialog(
            onDismissDialog = onDismissCancelDialog,
            onConfirm = {
                viewModel.reduce(ChangeRequestEvent.ClickCancelAssignment(request.id))
            },
            cancelState = cancelRequestState,
            requestNumber = request.requestNumber
        )
    }

    Column(modifier = modifier.padding(8.dp)) {
        PanelHeader(
            requestNumber = request.requestNumber,
            requestStatusId = request.status.id,
            onSettingsClick = { showEditDialog = true },
            onClose = onClosePanel,
            cancelAssignment = { showCancelAssignmentDialog = true }
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
                        text = "${request.createdAt.date}, ${
                            request.createdAt.hour.toString().padStart(2, '0')
                        }:${
                            request.createdAt.minute.toString().padStart(2, '0')
                        }:${request.createdAt.second.toString().padStart(2, '0')}"
                    )
                }

                Spacer(modifier = Modifier.height(SECTION_GAP))

                Section(header = stringResource(Res.string.cargo_info)) {
                    LabeledValue(
                        label = stringResource(Res.string.cargo_type),
                        value = request.cargo.type.name
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.weight),
                        value = request.cargo.weight.formatKg()
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.volume),
                        value = request.cargo.volume?.formatM3()
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.description),
                        value = request.cargo.description
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = SECTION_GAP, bottom = SECTION_GAP, end = 6.dp)
                )

                Section(header = stringResource(Res.string.loading_unloading_points)) {
                    LabeledValue(
                        label = stringResource(Res.string.loading_point),
                        value = request.loadingPoint.toStringAddress()
                    )
                    Spacer(modifier = Modifier.height(ITEM_GAP))
                    LabeledValue(
                        label = stringResource(Res.string.unloading_point),
                        value = request.unloadingPoint.toStringAddress()
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
                        text = request.customer.organizationName
                    )

                    request.customer.phoneNumber.let {
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
                        text = request.customer.email
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
                if (request.status.id == 1 || request.status.id == 2) {
                    isReassign = request.status.id == 2
                    Button(
                        onClick = { showDriverAssignmentDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(end = 6.dp)
                    ) {
                        val text = if (isReassign) {
                            "Переназначить водителя"
                        } else "Назначить водителя"
                        Text(text = text)
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