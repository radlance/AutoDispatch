package com.github.radlance.autodispatch.driver.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.retry
import autodispatch.composeapp.generated.resources.search_by_requests
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.request.common.presentation.CustomTextField
import com.github.radlance.autodispatch.request.core.presentation.BottomPagingBar
import com.github.radlance.autodispatch.request.core.presentation.RequestTable
import com.github.radlance.autodispatch.request.core.presentation.rememberDataTableScrollbarAdapter
import com.seanproctor.datatable.DataTableState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DriverViewModel = koinViewModel()
) {
    val driverUiState by viewModel.driverScreenState.collectAsState()
    val pageIndex = driverUiState.pageIndex
    val pageSize = driverUiState.pageSize
    val query = driverUiState.query

    val dataTableState = remember { DataTableState() }

    Row(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                CustomTextField(
                    value = query,
                    onValueChange = viewModel::onQueryChanged,
                    placeholder = stringResource(Res.string.search_by_requests),
                    leadingIcon = Icons.Default.Search,
                    labelText = null,
                    height = TextFieldDefaults.MinHeight,
                    modifier = Modifier.weight(1f)
                )
            }
            driverUiState.driversResultState.Reduce(
                onSuccess = {

                },
                onLoading = {
                    val previous = driverUiState.lastSuccessfulRequests
                    val driversToShow = previous?.items ?: emptyList()
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            // TODO drivers table
                            RequestTable(
                                requests = emptyList(),
                                onRequestClick = { req ->

                                },
                                dataTableState = dataTableState,
                                pageIndex = pageIndex,
                                selectedRequest = null,
                                showPanel = false,
                                pageSize = pageSize
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }

                            VerticalScrollbar(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp, top = 50.dp),
                                adapter = rememberDataTableScrollbarAdapter(
                                    scrollState = dataTableState.verticalScrollState
                                )
                            )
                        }

                        val totalCount = previous?.totalCount?.toInt() ?: 0
                        val start =
                            if (totalCount > 0) min(
                                pageIndex * pageSize + 1,
                                totalCount
                            ) else 0
                        val end =
                            if (totalCount > 0) min(start + pageSize - 1, totalCount) else 0
                        val pageCount =
                            if (totalCount > 0) (totalCount + pageSize - 1) / pageSize else 1

                        BottomPagingBar(
                            start = start,
                            end = end,
                            totalCount = totalCount,
                            pageIndex = pageIndex,
                            pageCount = pageCount,
                            onFirst = {},
                            onPrev = {},
                            onNext = {},
                            onLast = {},
                            onRefresh = {},
                            pageSize = pageSize,
                            pageSizeOptions = listOf(5, 10, 15, 20),
                            onPageSizeChange = {}
                        )
                    }
                },
                onError = { errorMsg ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = errorMsg, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    if (loadProfileUiState is FetchResultUiState.Error) {
                                        onReloadProfile()
                                    }
                                    viewModel.retryLoadRequests()
                                }
                            ) {
                                Text(stringResource(Res.string.retry))
                            }
                        }
                    }
                }
            )
        }
    }
}