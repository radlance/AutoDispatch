package com.github.radlance.autodispatch.driver.request.presentation

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
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import com.github.radlance.autodispatch.common.presentation.CustomDialog
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
fun DriverRequestAssignmentDialog(
    onDismiss: () -> Unit,
    onSuccessAssignDriver: () -> Unit,
    onStateReassignError: (String) -> Unit,
    driver: Driver,
    modifier: Modifier = Modifier,
    viewModel: DriverRequestAssignmentViewModel = koinViewModel()
) {
    val assignRequestState by viewModel.assignRequestState.collectAsState()
    val historyState by viewModel.state.collectAsStateWithLifecycle()
    val isSearchVisible by remember {
        derivedStateOf {
            !historyState.isEmptyResult
        }
    }
    val isRequestsLoading = historyState.paginatorState.itemsState is FetchResultUiState.Loading

    val isAssignLoading = assignRequestState is FetchResultUiState.Loading
    val assignError = (assignRequestState as? FetchResultUiState.Error<RequestError>)?.error

    val onDismiss = {
        onDismiss()
        viewModel.resetState()
    }

    LaunchedEffect(assignRequestState) {
        if (assignRequestState is FetchResultUiState.Success) {
            onDismiss()
            onSuccessAssignDriver()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadNextItems()
    }

    var selectedRequestId by remember { mutableStateOf<Int?>(null) }
    val state = historyState.paginatorState.itemsState

    val request = (state as? FetchResultUiState.Success)
        ?.data
        ?.find { it.id == selectedRequestId }

    val hasVehicle = driver.vehicle != null
    val isOverload = request != null && hasVehicle &&
            request.cargo.weight > driver.vehicle.payloadCapacity

    val canAssign = !isRequestsLoading && !isAssignLoading &&
            selectedRequestId != null &&
            hasVehicle &&
            !isOverload


    CustomDialog(
        modifier = modifier,
        onDismissRequest = { if (!isRequestsLoading) onDismiss() },
        title = {
            Text(
                text = "Назначение рейса водителю",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        content = {
            Box(Modifier.fillMaxWidth()) {
                Column {
                    assignError?.let {
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
                            if (assignError is RequestError.BaseError) {
                                Text(
                                    text = assignError.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                viewModel.resetState()
                                onStateReassignError(assignError.message)
                            }
                        }
                    }
                    if (isSearchVisible) {
                        DisableSelection {
                            CustomTextField(
                                value = historyState.query,
                                onValueChange = { viewModel.onQueryChange(it) },
                                placeholder = "Поиск по заявкам",
                                leadingIcon = Icons.Default.Search,
                                labelText = null,
                                height = TextFieldDefaults.MinHeight,
                                searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                    historyState.paginatorState.itemsState.Reduce(
                        onLoading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        },
                        onSuccess = { requests ->
                            if (requests.isNotEmpty()) {
                                val lazyListState = rememberLazyListState()

                                LaunchedEffect(lazyListState, requests.size) {
                                    snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                        .distinctUntilChanged()
                                        .collect { lastVisibleIndex ->
                                            if (lastVisibleIndex == requests.lastIndex && historyState.paginatorState.error == null) {
                                                viewModel.loadNextItems()
                                            }
                                        }
                                }
                                Text(
                                    text = buildAnnotatedString {
                                        append("Выберите заявку")
                                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                            append(" *")
                                        }
                                    },
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Box(modifier = Modifier.weight(1f)) {
                                    LazyColumn(
                                        state = lazyListState,
                                        verticalArrangement = Arrangement.spacedBy(24.dp),
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        items(items = requests, key = { it.id }) { request ->
                                            DriverRequestCard(
                                                selected = request.id == selectedRequestId,
                                                onSelect = { selectedRequestId = it },
                                                driverRequest = request
                                            )
                                        }
                                        if (historyState.paginatorState.isLoadingMore) {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }
                                        }

                                        if (historyState.paginatorState.error != null) {
                                            item {
                                                ErrorMessage(
                                                    message = historyState.paginatorState.error
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
                                if (historyState.isEmptyResult) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = modifier.fillMaxWidth().weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AssignmentTurnedIn,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp).alpha(0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Нет заявок для назначения",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.alpha(0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Здесь появятся заявки, ожидающие назначения водителя",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    }
                                } else {
                                    EmptySearchPlaceholder(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    )
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
                if (isAssignLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(AlertDialogDefaults.containerColor)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        buttons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedRequestId != null && (!hasVehicle || isOverload)) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = if (isOverload) "Перегруз" else "Автомобиль не назначен",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismiss, enabled = !isRequestsLoading) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    enabled = canAssign,
                    onClick = {
                        viewModel.assignRequest(requestId = request!!.id, driverId = driver.id)
                    }
                ) {
                    Text(text = "Назначить")
                }
            }
        }
    )
}