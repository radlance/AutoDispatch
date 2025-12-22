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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.utils.formatNumberNoTrailingZeros
import com.github.radlance.autodispatch.request.assignment.presentation.DriverAssignmentDialog
import com.github.radlance.autodispatch.request.change.presentation.CancelRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestEvent
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestFieldsUiState
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestViewModel
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Request
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val rejectDocumentsState by viewModel.rejectDocumentsState.collectAsState()
    val approveDocumentsState by viewModel.approveDocumentsState.collectAsState()

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(request.driverId != null) }
    var showCancelAssignmentDialog by remember { mutableStateOf(false) }
    var showStateErrorDialog by remember { mutableStateOf(false) }
    var stateErrorMessage by remember { mutableStateOf("") }
    var showRejectDocumentsDialog by remember { mutableStateOf(false) }
    var showApproveDocumentsDialog by remember { mutableStateOf(false) }
    var lastImageRetryAttempt by remember { mutableStateOf(0L) }

    val lazyRowState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    selectedImageUrl?.let {
        FullScreenImageDialog(
            onDismissRequest = { selectedImageUrl = null },
            selectedImageUrl = selectedImageUrl,
            documents = request.documents.map { it.imageUrl },
            onChangeImageIconClick = { selectedImageUrl = it }
        )
    }

    if (showStateErrorDialog) {
        val onDismiss: () -> Unit = {
            showStateErrorDialog = false
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
                Text(text = stateErrorMessage)
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
                onStateError = {
                    showStateErrorDialog = true
                    stateErrorMessage = it
                },
                requestStatusId = request.status.id,
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
            onSuccessAssignDriver = {
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
                showStateErrorDialog = true
                stateErrorMessage = it
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

        CancelRequestDialog(
            onDismissDialog = onDismissCancelDialog,
            onConfirm = {
                viewModel.reduce(ChangeRequestEvent.ClickCancelRequest(request.id))
            },
            onStateError = {
                onDismissCancelDialog()
                showStateErrorDialog = true
                stateErrorMessage = it
            },
            cancelState = cancelRequestState,
            requestNumber = request.requestNumber
        )
    }

    if (showRejectDocumentsDialog) {
        val onDismissRejectDialog = {
            showRejectDocumentsDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetRejectState)
        }

        LaunchedEffect(rejectDocumentsState) {
            if (rejectDocumentsState is FetchResultUiState.Success) {
                onSuccessCreateRequest()
                onDismissRejectDialog()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            }
        }
        RejectDocumentDialog(
            onDismissRequest = onDismissRejectDialog,
            onReject = {
                viewModel.reduce(
                    ChangeRequestEvent.ClickRejectDocument(
                        requestId = request.id,
                        rejectReason = it
                    )
                )
            },
            rejectState = rejectDocumentsState
        )
    }

    if (showApproveDocumentsDialog) {
        val onDismissApproveDialog = {
            showApproveDocumentsDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetApproveState)
        }

        LaunchedEffect(approveDocumentsState) {
            if (approveDocumentsState is FetchResultUiState.Success) {
                onSuccessCreateRequest()
                onDismissApproveDialog()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            }
        }
        ApproveDocumentDialog(
            onDismissRequest = onDismissApproveDialog,
            onApprove = {
                viewModel.reduce(
                    ChangeRequestEvent.ClickApproveDocument(
                        requestId = request.id
                    )
                )
            },
            approveState = approveDocumentsState
        )
    }

    DefaultPointerSelectionContainer {
        Column(modifier = modifier.padding(8.dp)) {
            RequestPanelHeader(
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
                    onShowDriverAssignmentDialog = { showDriverAssignmentDialog = it },
                    onShowRejectDocumentsDialog = { showRejectDocumentsDialog = it },
                    onShowApproveDocumentsDialog = { showApproveDocumentsDialog = it }
                )

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}