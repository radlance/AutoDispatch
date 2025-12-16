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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

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
//                                DriverHistoryMockItem(
//                                    id = historyItem.id.toString(),
//                                    index = history.indexOf(historyItem),
//                                    modifier = Modifier.padding(horizontal = 4.dp)
//                                )
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
                                .padding(horizontal = 18.dp), contentAlignment = Alignment.Center
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
                                    text = "История доставок пуста",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.alpha(0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Здесь появятся завершённые и отменённые доставки",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.5f)
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
        },
        confirmButton = {},
        dismissButton = {},
    )
}