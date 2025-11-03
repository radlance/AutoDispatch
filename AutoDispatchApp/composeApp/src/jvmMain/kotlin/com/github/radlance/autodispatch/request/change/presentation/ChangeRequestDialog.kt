package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import autodispatch.composeapp.generated.resources.create
import autodispatch.composeapp.generated.resources.creating_new_request
import autodispatch.composeapp.generated.resources.edit
import autodispatch.composeapp.generated.resources.remove
import autodispatch.composeapp.generated.resources.request_editing
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
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
    isEditRequest: Boolean = false,
    currentFieldsUiState: ChangeRequestFieldsUiState = ChangeRequestFieldsUiState(),
    viewModel: ChangeRequestViewModel = koinViewModel()
) {
    var showRemoveDialog by remember { mutableStateOf(false) }
    val fieldsUiState by viewModel.fieldsUiState.collectAsState()
    val customers by viewModel.customersState.collectAsState()
    val changeRequestState by viewModel.changeRequestState.collectAsState()
    val removeRequestState by viewModel.removeRequestState.collectAsState()
    val isLoadingRemove = removeRequestState is FetchResultUiState.Loading
    val errorRemove = (removeRequestState as? FetchResultUiState.Error<String>)?.error

    val scrollState = rememberScrollState()
    val screenHeight = LocalWindowInfo.current.containerSize.height
    val maxDialogHeight = screenHeight * 0.6f

    if (showRemoveDialog) {
        val onDismissRemoveDialog = {
            showRemoveDialog = false
            viewModel.reduce(ChangeRequestEvent.ResetRemoveState)
        }

        LaunchedEffect(removeRequestState) {
            if (removeRequestState is FetchResultUiState.Success) {
                onSuccessCreateRequest()
                viewModel.reduce(ChangeRequestEvent.ResetRemoveState)
                viewModel.reduce(ChangeRequestEvent.ResetChangeState)
                onDismissRemoveDialog()
            }
        }

        AlertDialog(
            onDismissRequest = {
                if (!isLoadingRemove) {
                    onDismissRemoveDialog()
                }
            },
            title = {
                Text(text = "Удаление заявки")
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(visible = errorRemove != null) {
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
                                text = errorRemove ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            buildAnnotatedString {
                                append("Вы уверены что хотите удалить заявку ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(fieldsUiState.requestNumber)
                                }
                                append("? Это действие нельзя отменить")
                            }
                        )
                        if (isLoadingRemove) {
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
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRemoveDialog, enabled = !isLoadingRemove) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reduce(ChangeRequestEvent.ClickRemove(fieldsUiState.requestId!!))
                    },
                    enabled = !isLoadingRemove
                ) {
                    Text(text = stringResource(Res.string.remove))
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (isEditRequest) {
            viewModel.reduce(ChangeRequestEvent.SetupFieldsState(currentFieldsUiState))
        }
    }

    LaunchedEffect(changeRequestState) {
        if (changeRequestState is FetchResultUiState.Success) {
            onSuccessCreateRequest()
            viewModel.reduce(ChangeRequestEvent.ResetChangeState)
            onDismiss()
        }
    }

    val isLoadingChange = changeRequestState is FetchResultUiState.Loading
    val errorChange = (changeRequestState as? FetchResultUiState.Error<String>)?.error

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoadingChange) {
                onDismiss()
            }
        },
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
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(visible = errorChange != null) {
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
                            text = errorChange ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().heightIn(max = maxDialogHeight.dp)) {
                    ChangeRequestFields(
                        cities = cities,
                        cargoTypes = cargoTypes,
                        customers = customers,
                        onEvent = viewModel::reduce,
                        scrollState = scrollState,
                        fieldsUiState = fieldsUiState
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
            }
        },
        confirmButton = {},
        dismissButton = {
            val buttonLabelRes = if (isEditRequest) {
                Res.string.edit
            } else Res.string.create
            Row {
                if (isEditRequest) {
                    OutlinedButton(
                        onClick = { showRemoveDialog = true },
                        enabled = !isLoadingChange
                    ) {
                        Text(text = stringResource(Res.string.remove))
                    }
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismiss, enabled = !isLoadingChange) {
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
                                && cargoWeightFieldValue.isNotBlank()
                                && loadingFieldValue.isNotBlank()
                                && unloadingFieldValue.isNotBlank()
                                && !isLoadingChange
                    },
                    onClick = {
                        if (isEditRequest) {
                            if (fieldsUiState == currentFieldsUiState) {
                                onDismiss()
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
                                    cargoLoading = loadingFieldValue,
                                    cargoUnloading = unloadingFieldValue,
                                    additionalInfo = additionalInfoFieldValue,
                                    requestId = requestId
                                )
                            )
                        }
                    }
                ) {
                    Text(text = stringResource(buttonLabelRes))
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}