package com.github.radlance.autodispatch.driver.assignment.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.attach
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.reassign
import com.github.radlance.autodispatch.common.domain.DriverStatus
import com.github.radlance.autodispatch.common.presentation.ExpandedCustomDialog
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.driver.core.domain.Driver
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleAssignmentDialog(
    onDismiss: () -> Unit,
    driver: Driver,
    onSuccessAssignDriver: () -> Unit,
    onStateError: (String) -> Unit,
    modifier: Modifier = Modifier,
    isReassign: Boolean,
    assignedVehicleId: Int?,
    viewModel: VehicleAssignmentViewModel = koinViewModel()
) {
    val vehicleAssignmentsState by viewModel.state.collectAsState()
    val assignDriverState by viewModel.assignDriverState.collectAsState()
    val assignRequestState by viewModel.assignDriverState.collectAsState()
    val isLoading = assignRequestState is FetchResultUiState.Loading
    val error = (assignRequestState as? FetchResultUiState.Error)?.error
    val isSearchVisible by remember {
        derivedStateOf {
            !vehicleAssignmentsState.isEmptyResult
        }
    }
    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }

    val onDismissAction = {
        onDismiss()
        viewModel.resetState()
    }

    LaunchedEffect(Unit) {
        viewModel.loadNextItems()
    }

    val successVehicle =
        (vehicleAssignmentsState.paginatorState.itemsState as? FetchResultUiState.Success)?.data?.find { it.id == selectedVehicleId }
    val isDriverSelected = successVehicle != null
    val hasDriverChanged = successVehicle?.id != assignedVehicleId
    val isButtonEnabled =
        isDriverSelected && !isLoading && (!isReassign || hasDriverChanged) && (!isReassign || driver.status == DriverStatus.Free)

    ExpandedCustomDialog(
        allowDismiss = !isLoading,
        onDismissRequest = onDismissAction,
        title = {
            Text(
                text = "Назначение автомобиля",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        content = { requestDismiss ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
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
                            requestDismiss()
                            onStateError(error.message)
                        }
                    }
                }
                Card {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Text("Водитель", modifier = Modifier.alpha(0.7f))
                        Spacer(Modifier.height(12.dp))
                        Text(driver.fullName, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            driver.phoneNumber,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
                if (isSearchVisible) {
                    DisableSelection {
                        CustomTextField(
                            value = vehicleAssignmentsState.query,
                            onValueChange = { viewModel.onQueryChange(it) },
                            placeholder = "Поиск по доступным автомобилям",
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            height = TextFieldDefaults.MinHeight,
                            searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
                vehicleAssignmentsState.paginatorState.itemsState.Reduce(
                    onLoading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    onSuccess = { vehicles ->
                        if (vehicles.isNotEmpty()) {
                            val lazyListState = rememberLazyListState()
                            LaunchedEffect(lazyListState, vehicles.size) {
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .distinctUntilChanged()
                                    .collect { lastVisibleIndex ->
                                        if (lastVisibleIndex == vehicles.lastIndex && vehicleAssignmentsState.paginatorState.error == null) {
                                            viewModel.loadNextItems()
                                        }
                                    }
                            }
                            Box(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(
                                    state = lazyListState,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(items = vehicles, key = { it.id }) { vehicle ->
                                        VehicleAssignmentCard(
                                            selected = vehicle.id == selectedVehicleId,
                                            onSelect = { selectedVehicleId = it },
                                            vehicle = vehicle
                                        )
                                    }
                                    if (vehicleAssignmentsState.paginatorState.isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }

                                    if (vehicleAssignmentsState.paginatorState.error != null) {
                                        item {
                                            ErrorMessage(
                                                message = vehicleAssignmentsState.paginatorState.error
                                                    ?: "Ошибка загрузки",
                                                onRetry = viewModel::loadNextItems

                                            )
                                        }
                                    }
                                }
                                VerticalScrollbar(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                        .offset(x = 8.dp),
                                    adapter = rememberScrollbarAdapter(scrollState = lazyListState)
                                )
                            }
                        } else {
                            if (vehicleAssignmentsState.isEmptyResult) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalShipping,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp).alpha(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Нет доступного транспорта",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.alpha(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Здесь появится транспорт, готовый принять заявку",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.alpha(0.5f)
                                    )
                                }
                            } else {
                                EmptySearchPlaceholder(
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }
                        }
                        LaunchedEffect(assignedVehicleId) {
                            if (isReassign) {
                                selectedVehicleId = vehicles.find {
                                    it.id == assignedVehicleId
                                }?.id
                            }
                        }
                    },
                    onError = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ErrorMessage(
                                message = it,
                                onRetry = viewModel::loadNextItems
                            )
                        }
                    }
                )
            }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AlertDialogDefaults.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LaunchedEffect(assignDriverState) {
                if (assignDriverState is FetchResultUiState.Success) {
                    onSuccessAssignDriver()
                    requestDismiss()
                }
            }
        },

        buttons = { requestDismiss ->
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = requestDismiss, enabled = !isLoading) {
                Text(text = stringResource(Res.string.cancel))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    viewModel.assignVehicle(
                        driverId = driver.id,
                        vehicleId = selectedVehicleId!!,
                        reassign = isReassign
                    )
                },
                enabled = isButtonEnabled
            ) {
                val text = if (isReassign) {
                    Res.string.reassign
                } else Res.string.attach
                Text(text = stringResource(text))
            }
        },
        modifier = modifier
    )
}