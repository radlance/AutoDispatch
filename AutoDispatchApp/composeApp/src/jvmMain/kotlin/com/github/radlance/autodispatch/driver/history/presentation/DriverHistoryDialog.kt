package com.github.radlance.autodispatch.driver.history.presentation

import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.ExpandedCustomDialog
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptyHistoryPlaceholder
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.domain.Driver
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
    val historyState by viewModel.state.collectAsStateWithLifecycle()
    val isSearchVisible by remember {
        derivedStateOf {
            !historyState.isEmptyResult
        }
    }
    val isLoading = historyState.paginatorState.itemsState is FetchResultUiState.Loading

    LaunchedEffect(Unit) {
        viewModel.loadNextItems(driverId = driver.id)
    }

    ExpandedCustomDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        onFinish = viewModel::resetState,
        title = { requestDismiss ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История доставок водителя",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                IconButton(
                    enabled = !isLoading,
                    onClick = requestDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
        content = {
            Column {
                if (isSearchVisible) {
                    DisableSelection {
                        CustomTextField(
                            value = historyState.query,
                            onValueChange = { viewModel.onQueryChange(it) },
                            placeholder = "Поиск по доставкам",
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            height = TextFieldDefaults.MinHeight,
                            searchBarColors = SearchBarDefaults.colors(containerColor = CardDefaults.cardColors().containerColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                    }
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

                            LaunchedEffect(lazyListState, history.size) {
                                snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .distinctUntilChanged()
                                    .collect { lastVisibleIndex ->
                                        if (lastVisibleIndex == history.lastIndex && historyState.paginatorState.error == null) {
                                            viewModel.loadNextItems(driverId = driver.id)
                                        }
                                    }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                LazyColumn(
                                    state = lazyListState,
                                    verticalArrangement = Arrangement.spacedBy(24.dp),
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
                                EmptyHistoryPlaceholder(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                )
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
                                onRetry = { viewModel.loadNextItems(driverId = driver.id) }
                            )
                        }
                    }
                )
            }
        },
        buttons = {}
    )
}