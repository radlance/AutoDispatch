package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.utils.formatNumberNoTrailingZeros
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
import org.koin.compose.viewmodel.koinViewModel

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
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    val lazyRowState = rememberLazyListState()
    val cancelRequestState by viewModel.cancelRequestState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(false) }
    var showCancelAssignmentDialog by remember { mutableStateOf(false) }
    var showReassignErrorDialog by remember { mutableStateOf(false) }
    var reassignErrorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var lastImageRetryAttempt by remember { mutableStateOf(0L) }

    selectedImageUrl?.let {
        FullScreenImageDialog(
            onDismissRequest = { selectedImageUrl = null },
            selectedImageUrl = selectedImageUrl,
            documents = request.documents,
            onChangeImageIconClick = { selectedImageUrl = it }
        )
    }

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
            RequestDetailsSections(
                scrollState = scrollState,
                lazyRowState = lazyRowState,
                request = request,
                lastImageRetryAttempt = lastImageRetryAttempt,
                onReloadImage = { lastImageRetryAttempt = it },
                onSelectImageUrl = { selectedImageUrl = it },
                isReassign = isReassign,
                onChangeReassign = { isReassign = it },
                onShowDriverAssignmentDialog = { showDriverAssignmentDialog = it }
            )

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}