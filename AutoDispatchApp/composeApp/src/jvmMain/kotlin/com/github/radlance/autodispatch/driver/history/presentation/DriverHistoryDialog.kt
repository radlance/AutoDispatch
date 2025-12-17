package com.github.radlance.autodispatch.driver.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.domain.Driver
import com.github.radlance.autodispatch.request.common.presentation.CustomTextField
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverHistoryDialog(
    onDismiss: () -> Unit,
    driver: Driver,
    modifier: Modifier = Modifier,
    viewModel: DriverHistoryViewModel = koinViewModel()
) {
    val historyState by viewModel.driverHistoryState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadNextItems(driverId = driver.id)
    }

    val onDismiss = {
        onDismiss()
        viewModel.resetState()
    }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История доставок водителя",
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
        text = {
            Column {
                DisableSelection {
                    CustomTextField(
                        value = historyState.query,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        placeholder = "Поиск по доставкам",
                        leadingIcon = Icons.Default.Search,
                        labelText = null,
                        height = TextFieldDefaults.MinHeight,
                        searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                }
                historyState.paginatorState.itemsState.Reduce(
                    onLoading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    onSuccess = { history ->
                        if (history.isNotEmpty()) {
                            val lazyListState = rememberLazyListState()
                            val historyItems =
                                (historyState.paginatorState.itemsState as? FetchResultUiState.Success)?.data.orEmpty()

                            LaunchedEffect(lazyListState, historyItems.size) {
                                if (historyItems.isEmpty()) return@LaunchedEffect
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .distinctUntilChanged()
                                    .collect { lastVisibleIndex ->
                                        if (lastVisibleIndex == historyItems.lastIndex && historyState.paginatorState.error == null) {
                                            viewModel.loadNextItems(driverId = driver.id)
                                        }
                                    }
                            }

                            LazyColumn(
                                state = lazyListState,
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(items = history, key = { it.id }) { historyItem ->
                                    DriverHistoryCard(
                                        driverHistory = historyItem
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
                                            onRetry = { viewModel.loadNextItems(driverId = driver.id) }

                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                                    .padding(horizontal = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp).alpha(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Доставок не найдено",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.alpha(0.7f)
                                    )
                                }
                            }
                        }
                    },
                    onError = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ErrorMessage(
                                message = it,
                                onRetry = { viewModel.loadNextItems(driverId = driver.id) }
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}