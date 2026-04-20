package com.github.radlance.autodispatch.request.assignment.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.assign
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.driver_assignment
import autodispatch.composeapp.generated.resources.reassign
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.ExpandedCustomDialog
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.common.utils.formatKg
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.core.domain.Request
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverAssignmentDialog(
    onDismiss: () -> Unit,
    request: Request,
    onSuccessAssignDriver: () -> Unit,
    onStateReassignError: (String) -> Unit,
    modifier: Modifier = Modifier,
    isReassign: Boolean,
    assignedDriverId: Int?,
    viewModel: DriverAssignmentViewModel = koinViewModel()
) {
    val driverAssignmentsState by viewModel.state.collectAsState()
    val assignRequestState by viewModel.assignRequestState.collectAsState()
    val isLoading = assignRequestState is FetchResultUiState.Loading
    val error = (assignRequestState as? FetchResultUiState.Error)?.error
    val isSearchVisible by remember {
        derivedStateOf {
            !driverAssignmentsState.isEmptyResult
        }
    }
    var selectedDriverId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadNextItems()
    }

    ExpandedCustomDialog(
        allowDismiss = !isLoading,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.driver_assignment),
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
                            viewModel.resetState()
                            onStateReassignError(error.message)
                        }
                    }
                }
                Card {
                    Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                        Text("Заявка ${request.requestNumber}", modifier = Modifier.alpha(0.7f))
                        Spacer(Modifier.height(12.dp))
                        Text("${request.origin} → ${request.destination}", fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "${request.cargo.type.name} • ${request.cargo.weight.formatKg()} • ${request.createdAt.date}",
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
                if (isSearchVisible) {
                    DisableSelection {
                        CustomTextField(
                            value = driverAssignmentsState.query,
                            onValueChange = { viewModel.onQueryChange(it) },
                            placeholder = "Поиск по водителям",
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            height = TextFieldDefaults.MinHeight,
                            searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
                driverAssignmentsState.paginatorState.itemsState.Reduce(
                    onLoading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    onSuccess = { stats ->
                        if (stats.isNotEmpty()) {
                            val lazyListState = rememberLazyListState()
                            LaunchedEffect(lazyListState, stats.size) {
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .distinctUntilChanged()
                                    .collect { lastVisibleIndex ->
                                        if (lastVisibleIndex == stats.lastIndex && driverAssignmentsState.paginatorState.error == null) {
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
                                    items(items = stats, key = { it.driverId }) { driverStats ->
                                        DriverAssignmentCard(
                                            selected = driverStats.driverId == selectedDriverId,
                                            onSelect = { selectedDriverId = it },
                                            driverStats = driverStats
                                        )
                                    }
                                    if (driverAssignmentsState.paginatorState.isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }

                                    if (driverAssignmentsState.paginatorState.error != null) {
                                        item {
                                            ErrorMessage(
                                                message = driverAssignmentsState.paginatorState.error
                                                    ?: "Ошибка загрузки",
                                                onRetry = viewModel::loadNextItems

                                            )
                                        }
                                    }
                                }
                                if (!isLoading) {
                                    VerticalScrollbar(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .fillMaxHeight()
                                            .offset(x = 8.dp),
                                        adapter = rememberScrollbarAdapter(scrollState = lazyListState)
                                    )
                                }
                            }
                        } else {
                            if (driverAssignmentsState.isEmptyResult) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp).alpha(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Нет доступных водителей",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.alpha(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Здесь появятся водители, готовые принять заявки",
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
                        LaunchedEffect(assignedDriverId) {
                            if (isReassign) {
                                selectedDriverId = stats.find {
                                    it.driverId == assignedDriverId
                                }?.driverId
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
            LaunchedEffect(assignRequestState) {
                if (assignRequestState is FetchResultUiState.Success) {
                    onSuccessAssignDriver()
                    requestDismiss()
                }
            }
        },
        onFinish = viewModel::resetState,
        buttons = { requestDismiss ->
            val stats =
                (driverAssignmentsState.paginatorState.itemsState as? FetchResultUiState.Success)?.data?.find { it.driverId == selectedDriverId }
            val isDriverSelected = stats != null
            val hasDriverChanged = stats?.driverId != assignedDriverId
            val vehicleCapacity = stats?.vehiclePayloadCapacity
            val isOverweight = vehicleCapacity != null &&
                    request.cargo.weight > vehicleCapacity
            val isOutOfSchedule = stats?.isWorkingNow == false
            val outOfScheduleStats = stats?.takeIf { !it.isWorkingNow }
            val isButtonEnabled =
                isDriverSelected &&
                        !isLoading &&
                        (!isReassign || hasDriverChanged) &&
                        stats.vehicleModel != null &&
                        vehicleCapacity != null &&
                        !isOverweight &&
                        !isOutOfSchedule

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                stats?.vehiclePayloadCapacity?.let {
                    if (request.cargo.weight > it) {
                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Перегруз",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (outOfScheduleStats != null) {
                    Text(
                        text = outOfScheduleStats.scheduleHint,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }

                TextButton(onClick = requestDismiss, enabled = !isLoading) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        viewModel.assignRequest(
                            requestId = request.id,
                            driverId = stats!!.driverId,
                            isReassign = isReassign
                        )
                    },
                    enabled = isButtonEnabled
                ) {
                    val text = if (isReassign) {
                        Res.string.reassign
                    } else Res.string.assign
                    Text(text = stringResource(text))
                }
            }
        },
        modifier = modifier
    )
}
