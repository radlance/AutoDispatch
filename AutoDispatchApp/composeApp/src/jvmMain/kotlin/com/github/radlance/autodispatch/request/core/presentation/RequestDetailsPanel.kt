package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.utils.formatNumberNoTrailingZeros
import com.github.radlance.autodispatch.request.assignment.presentation.DriverAssignmentDialog
import com.github.radlance.autodispatch.request.change.presentation.CancelRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestDialog
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestEvent
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestFieldsUiState
import com.github.radlance.autodispatch.request.change.presentation.ChangeRequestViewModel
import com.github.radlance.autodispatch.request.change.presentation.DriverUnassignmentDialog
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import com.github.radlance.autodispatch.request.core.domain.Request
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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
    val driverUnassignmentState by viewModel.driverUnassignmentState.collectAsState()

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDriverAssignmentDialog by remember { mutableStateOf(false) }
    var isReassign by remember { mutableStateOf(request.driverId != null) }
    var showCancelAssignmentDialog by remember { mutableStateOf(false) }
    var showStateErrorDialog by remember { mutableStateOf(false) }
    var stateErrorMessage by remember { mutableStateOf("") }
    var showRejectDocumentsDialog by remember { mutableStateOf(false) }
    var showApproveDocumentsDialog by remember { mutableStateOf(false) }
    var showDriverUnassignmentDialog by remember { mutableStateOf(false) }
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
        CustomDialog(
            onDismissRequest = onDismiss,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Ошибка")
                }
            },
            content = {
                Text(text = stateErrorMessage)
            },
            buttons = { dismissRequest ->
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = dismissRequest
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

        var rejectionReasonFieldValue by remember { mutableStateOf("") }
        val isLoading = rejectDocumentsState is FetchResultUiState.Loading
        val error = (rejectDocumentsState as? FetchResultUiState.Error<String>)?.error

        CustomDialog(
            onDismissRequest = {
                if (!isLoading) {
                    showRejectDocumentsDialog = false
                }
            },
            onFinish = {
                viewModel.reduce(ChangeRequestEvent.ResetRejectState)
            },
            title = {
                Text(text = "Отклонить документы", style = MaterialTheme.typography.titleLarge)
            },
            content = { requestDismiss ->
                Column {
                    error?.let {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Text(text = "Укажите причину отклонения документов. Водитель получит уведомление и должен будет отправить документы повторно.")
                    Spacer(Modifier.height(24.dp))
                    CustomTextField(
                        labelText = "Причина отклонения",
                        value = rejectionReasonFieldValue,
                        onValueChange = { rejectionReasonFieldValue = it },
                        placeholder = "Например: на фото ТНН не видна подпись получателя…",
                        modifier = Modifier.fillMaxWidth(),
                        isRequired = false,
                        singleLine = false,
                        placeholderFontSize = 14.sp,
                        searchBarColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                LaunchedEffect(rejectDocumentsState) {
                    if (rejectDocumentsState is FetchResultUiState.Success) {
                        onSuccessCreateRequest()
                        requestDismiss()
                        scope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    }
                }
            },
            buttons = { requestDismiss ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = requestDismiss, enabled = !isLoading) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                TextButton(
                    onClick = {
                        viewModel.reduce(
                            ChangeRequestEvent.ClickRejectDocument(
                                requestId = request.id,
                                rejectReason = rejectionReasonFieldValue
                            )
                        )
                    },
                    enabled = !isLoading && rejectionReasonFieldValue.isNotBlank()
                ) {
                    Text(text = "Отклонить")
                }
            }
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

    if (showDriverUnassignmentDialog) {
        val onDismissUnassignmentDialog = {
            showDriverUnassignmentDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetDriverUnassignmentState)
        }

        LaunchedEffect(driverUnassignmentState) {
            if (driverUnassignmentState is FetchResultUiState.Success) {
                onSuccessCreateRequest()
                onDismissUnassignmentDialog()
                scope.launch {
                    scrollState.animateScrollTo(0)
                }
            }
        }
        DriverUnassignmentDialog(
            onDismissRequest = { showDriverUnassignmentDialog = false },
            onConfirm = {
                viewModel.reduce(ChangeRequestEvent.ClickUnassignDriver(request.id))

            },
            onStateError = {
                onDismissUnassignmentDialog()
                showStateErrorDialog = true
                stateErrorMessage = it
            },
            unassignmentState = driverUnassignmentState
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
                    showDriverAssignmentDialog = { showDriverAssignmentDialog = true },
                    showRejectDocumentsDialog = { showRejectDocumentsDialog = true },
                    showApproveDocumentsDialog = { showApproveDocumentsDialog = true },
                    showDriverUnassignmentDialog = { showDriverUnassignmentDialog = true }
                )

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}