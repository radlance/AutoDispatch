package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.cancel_variant
import autodispatch.composeapp.generated.resources.create
import autodispatch.composeapp.generated.resources.creating_new_request
import autodispatch.composeapp.generated.resources.edit
import autodispatch.composeapp.generated.resources.request_cancellation
import autodispatch.composeapp.generated.resources.request_editing
import autodispatch.composeapp.generated.resources.you_want_to_cancel_request
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.core.domain.CargoType
import com.github.radlance.autodispatch.request.core.domain.City
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangeRequestDialog(
    cities: List<City>,
    cargoTypes: List<CargoType>,
    onDismiss: () -> Unit,
    onSuccessCreateRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onStateError: (String) -> Unit = {},
    requestStatusId: Int? = null,
    currentFieldsUiState: ChangeRequestFieldsUiState = ChangeRequestFieldsUiState(),
    viewModel: ChangeRequestViewModel = koinViewModel()
) {
    val isEditRequest = requestStatusId == 1 || requestStatusId == 2
    val onDismiss = {
        onDismiss()
        viewModel.reduce(event = ChangeRequestEvent.ResetChangeState)
    }
    var closeBaseDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()
    val customers by viewModel.customersState.collectAsState()
    val changeRequestState by viewModel.changeRequestState.collectAsState()
    val cancelRequestState by viewModel.cancelRequestState.collectAsState()
    val removeRequestState by viewModel.removeRequestState.collectAsState()

    val scrollState = rememberScrollState()
    val screenHeight = LocalWindowInfo.current.containerSize.height
    val maxDialogHeight = screenHeight * 0.6f

    if (showCancelDialog) {
        val onDismissCancelDialog = {
            showCancelDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetCancelState)
        }

        val isLoading = cancelRequestState is FetchResultUiState.Loading
        val error = (cancelRequestState as? FetchResultUiState.Error)?.error

        CustomDialog(
            allowDismiss = !isLoading,
            onDismissRequest = {
                onDismissCancelDialog()
            },
            title = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(Res.string.request_cancellation),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            content = { requestDismiss ->
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            if (error is RequestError.BaseError) {
                                Text(
                                    text = error.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                onStateError(error.message)
                            }
                        }
                    }

                    Text(
                        buildAnnotatedString {
                            append("${stringResource(Res.string.you_want_to_cancel_request)} ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(fieldsUiState.requestNumber)
                            }
                            append("?")
                        }
                    )
                }
                LaunchedEffect(cancelRequestState) {
                    if (cancelRequestState is FetchResultUiState.Success) {
                        requestDismiss()
                        closeBaseDialog = true
                    }
                }
            },
            buttons = { dismissRequest ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = dismissRequest, enabled = !isLoading) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        viewModel.reduce(
                            ChangeRequestEvent.ClickCancelRequest(
                                fieldsUiState.requestId!!
                            )
                        )
                    },
                    enabled = !isLoading
                ) {
                    Text(text = stringResource(Res.string.cancel_variant))
                }
            }
        )
    }

    if (showRemoveDialog) {
        val isLoadingRemove = removeRequestState is FetchResultUiState.Loading
        val errorRemove = (removeRequestState as? FetchResultUiState.Error)?.error

        CustomDialog(
            modifier = modifier,
            allowDismiss = !isLoadingRemove,
            onDismissRequest = {
                showRemoveDialog = false
            },
            onFinish = { viewModel.reduce(ChangeRequestEvent.ResetRemoveState) },
            title = {
                Text(text = "Удаление заявки")
            },
            content = { requestDismiss ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    errorRemove?.let {
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
                            if (errorRemove is RequestError.BaseError) {
                                Text(
                                    text = errorRemove.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                onStateError(errorRemove.message)
                            }
                        }
                    }

                    Text(
                        buildAnnotatedString {
                            append("Вы уверены что хотите удалить заявку ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(fieldsUiState.requestNumber)
                            }
                            append("?")
                        }
                    )
                }

                LaunchedEffect(removeRequestState) {
                    if (removeRequestState is FetchResultUiState.Success) {
                        requestDismiss()
                        closeBaseDialog = true
                    }
                }
            },
            buttons = { dismissRequest ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = dismissRequest, enabled = !isLoadingRemove) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { viewModel.reduce(ChangeRequestEvent.ClickRemoveRequest(fieldsUiState.requestId!!)) },
                    enabled = !isLoadingRemove
                ) {
                    Text(text = "Удалить")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (isEditRequest) {
            viewModel.reduce(ChangeRequestEvent.SetupFieldsState(currentFieldsUiState))
        }
    }

    val isLoadingChange = changeRequestState is FetchResultUiState.Loading
    val errorChange = (changeRequestState as? FetchResultUiState.Error)?.error

    CustomDialog(
        modifier = modifier,
        allowDismiss = !isLoadingChange,
        onDismissRequest = onDismiss,
        title = {
            val title = if (isEditRequest) {
                buildAnnotatedString {
                    append(stringResource(Res.string.request_editing))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(fieldsUiState.requestNumber)
                    }
                }
            } else {
                buildAnnotatedString { append(stringResource(Res.string.creating_new_request)) }
            }

            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        content = { requestDismiss ->
            Column(modifier = Modifier.fillMaxWidth()) {
                errorChange?.let {
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
                        if (errorChange is RequestError.BaseError) {
                            Text(
                                text = errorChange.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            requestDismiss()
                            onStateError(errorChange.message)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = maxDialogHeight.dp)) {
                    ChangeRequestFields(
                        cities = cities,
                        cargoTypes = cargoTypes,
                        customers = customers,
                        onEvent = viewModel::reduce,
                        scrollState = scrollState,
                        fieldsUiState = fieldsUiState,
                    )
                    if (!isLoadingChange) {
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState),
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .offset(x = 10.dp)
                        )
                    }

                    if (isLoadingChange) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    AlertDialogDefaults.containerColor
                                ).clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {},
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                LaunchedEffect(changeRequestState) {
                    if (changeRequestState is FetchResultUiState.Success) {
                        onSuccessCreateRequest()
                        requestDismiss()
                    }
                }

                LaunchedEffect(closeBaseDialog) {
                    if (closeBaseDialog) {
                        requestDismiss()
                    }
                }
            }
        },
        onFinish = {
            viewModel.reduce(ChangeRequestEvent.ResetChangeState)
            if (closeBaseDialog) {
                onSuccessCreateRequest()
                closeBaseDialog = false
            }
        },
        buttons = { requestDismiss ->
            Row {
                if (isEditRequest) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showCancelDialog = true },
                            enabled = !isLoadingChange
                        ) {
                            Text(stringResource(Res.string.cancel_variant))
                        }

                        if (requestStatusId == 1) {
                            TextButton(
                                onClick = { showRemoveDialog = true },
                                enabled = !isLoadingChange
                            ) {
                                Text(
                                    "Удалить",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = requestDismiss, enabled = !isLoadingChange) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    enabled = with(fieldsUiState) {
                        departureCity != null
                                && destinationCity != null
                                && cargoType != null
                                && companyNameFieldValue.isNotBlank()
                                && companyEmailFieldValue.isNotBlank()
                                && companyPhoneFieldValue.isNotBlank()
                                && cargoWeightFieldValue.isNotBlank()
                                && (loadingFieldLatValue != null && loadingFieldLonValue != null)
                                && (unloadingFieldLatValue != null && unloadingFieldLonValue != null)
                                && !isLoadingChange
                    },
                    onClick = {
                        if (isEditRequest) {
                            if (fieldsUiState == currentFieldsUiState) {
                                requestDismiss()
                                return@Button
                            }
                        } else {
                            if (fieldsUiState.requestId != null) {
                                viewModel.reduce(ChangeRequestEvent.ResetChangeState)
                                requestDismiss()
                                return@Button
                            }
                        }

                        with(fieldsUiState) {
                            viewModel.reduce(
                                ChangeRequestEvent.ClickCreate(
                                    originId = departureCity!!.id,
                                    destinationId = destinationCity!!.id,
                                    companyName = companyNameFieldValue,
                                    companyEmail = companyEmailFieldValue,
                                    companyPhone = companyPhoneFieldValue,
                                    cargoTypeId = cargoType!!.id,
                                    cargoWeight = cargoWeightFieldValue,
                                    cargoVolume = cargoVolumeFieldValue,
                                    cargoDescription = cargoDescriptionFieldValue,
                                    cargoLoadingAddress = loadingFieldAddressValue,
                                    cargoLoadingLat = loadingFieldLatValue!!,
                                    cargoLoadingLon = loadingFieldLonValue!!,
                                    cargoUnloadingAddress = unloadingFieldAddressValue,
                                    cargoUnloadingLat = unloadingFieldLatValue!!,
                                    cargoUnloadingLon = unloadingFieldLonValue!!,
                                    additionalInfo = additionalInfoFieldValue,
                                    requestId = requestId
                                )
                            )
                        }
                    }
                ) {
                    val buttonLabelRes = if (isEditRequest) {
                        Res.string.edit
                    } else Res.string.create
                    Text(text = stringResource(buttonLabelRes))
                }
            }
        }
    )
}