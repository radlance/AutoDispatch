package com.github.radlance.autodispatch.admin.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create
import autodispatch.composeapp.generated.resources.retry
import autodispatch.composeapp.generated.resources.search_by_users
import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserManagementFilters
import com.github.radlance.autodispatch.common.presentation.AppBackHandler
import com.github.radlance.autodispatch.common.presentation.CustomTextField
import com.github.radlance.autodispatch.common.presentation.EmptySearchPlaceholder
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.request.core.presentation.BottomPagingBar
import com.github.radlance.autodispatch.request.core.presentation.rememberDataTableScrollbarAdapter
import com.seanproctor.datatable.DataTableState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserManagementScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserManagementViewModel = koinViewModel()
) {
    var showUserDetailsPanel by rememberSaveable { mutableStateOf(false) }
    var showSearchFilters by rememberSaveable { mutableStateOf(false) }
    var selectedUser by rememberSaveable { mutableStateOf<UserDetailed?>(null) }

    val screenState by viewModel.userManagementScreenState.collectAsState()
    val pageIndex = screenState.pageIndex
    val pageSize = screenState.pageSize
    val query = screenState.query
    val selectedRoles = screenState.selectedRoles
    val selectedStatuses = screenState.selectedStatuses

    val dataTableState = remember { DataTableState() }
    val scope = rememberCoroutineScope()

    AppBackHandler {
        if (showUserDetailsPanel) {
            showUserDetailsPanel = false
        }
    }

    Row(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            screenState.filters.Reduce(
                onLoading = {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                },
                onSuccess = { filters ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        CustomTextField(
                            value = query,
                            onValueChange = viewModel::onQueryChanged,
                            placeholder = stringResource(Res.string.search_by_users),
                            leadingIcon = Icons.Default.Search,
                            labelText = null,
                            height = TextFieldDefaults.MinHeight,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        FilledTonalIconToggleButton(
                            checked = showSearchFilters,
                            onCheckedChange = {
                                if (showSearchFilters) {
                                    scope.launch {
                                        dataTableState.verticalScrollState.scrollTo(0)
                                    }
                                }
                                showSearchFilters = it
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = null
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = { /* TODO: Creation not required yet */ }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(Res.string.create))
                        }
                    }

                    AnimatedVisibility(
                        visible = showSearchFilters,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                        UserManagementFilters(
                            selectedStatuses = selectedStatuses,
                            selectedRoles = selectedRoles,
                            statuses = filters.statuses,
                            roles = filters.roles,
                            onStatusesChanged = viewModel::onStatusesChanged,
                            onRolesChanged = viewModel::onRolesChanged,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                        )
                    }

                    screenState.usersResultState.Reduce(
                        onSuccess = { usersResult ->
                            val usersToShow = usersResult.items
                            selectedUser?.let { selected ->
                                val foundUser = usersToShow.find { u -> u.id == selected.id }
                                if (foundUser == null) {
                                    selectedUser = null
                                    showUserDetailsPanel = false
                                } else if (foundUser != selected) {
                                    selectedUser = foundUser
                                }
                            }

                            if (usersToShow.isEmpty()) {
                                EmptySearchPlaceholder(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                )
                            } else {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        UserManagementTable(
                                            users = usersToShow,
                                            onUserClick = { user ->
                                                showUserDetailsPanel =
                                                    if (user == selectedUser) {
                                                        !showUserDetailsPanel
                                                    } else true

                                                selectedUser = user
                                            },
                                            dataTableState = dataTableState,
                                            pageIndex = pageIndex,
                                            selectedUser = selectedUser,
                                            showPanel = showUserDetailsPanel,
                                            pageSize = pageSize
                                        )

                                        VerticalScrollbar(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 4.dp, top = 50.dp),
                                            adapter = rememberDataTableScrollbarAdapter(
                                                scrollState = dataTableState.verticalScrollState
                                            )
                                        )
                                    }

                                    val totalCount = usersResult.totalCount.toInt()
                                    val start = min(pageIndex * pageSize + 1, totalCount)
                                    val end = min(start + pageSize - 1, totalCount)
                                    val pageCount = (totalCount + pageSize - 1) / pageSize

                                    BottomPagingBar(
                                        start = start,
                                        end = end,
                                        totalCount = totalCount,
                                        pageIndex = pageIndex,
                                        pageCount = pageCount,
                                        onFirst = { viewModel.onPageIndexChanged(0) },
                                        onPrev = { viewModel.onPageIndexChanged(pageIndex - 1) },
                                        onNext = { viewModel.onPageIndexChanged(pageIndex + 1) },
                                        onLast = { viewModel.onPageIndexChanged(pageCount - 1) },
                                        onRefresh = { viewModel.triggerRequestLoad() },
                                        pageSize = pageSize,
                                        pageSizeOptions = listOf(5, 10, 15, 20, 25),
                                        onPageSizeChange = { viewModel.onPageSizeChanged(it) }
                                    )
                                }
                            }
                        },
                        onLoading = {
                            val previous = screenState.lastSuccessfulRequest
                            val usersToShow = previous?.items ?: emptyList()
                            Column(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    UserManagementTable(
                                        users = usersToShow,
                                        onUserClick = { user ->
                                            showUserDetailsPanel = if (user == selectedUser) {
                                                !showUserDetailsPanel
                                            } else true

                                            selectedUser = user
                                        },
                                        dataTableState = dataTableState,
                                        pageIndex = pageIndex,
                                        selectedUser = selectedUser,
                                        showPanel = showUserDetailsPanel,
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
                                            viewModel.retryLoadRequests()
                                        }
                                    ) {
                                        Text(stringResource(Res.string.retry))
                                    }
                                }
                            }
                        }
                    )
                },
                onError = {
                    ErrorMessage(
                        message = it,
                        onRetry = {
                            viewModel.loadFilters()
                            if (loadProfileUiState is FetchResultUiState.Error) {
                                onReloadProfile()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }

        val success =
            (screenState.filters as? FetchResultUiState.Success<UserManagementFilters>)?.data
        success?.let {
            AnimatedVisibility(
                visible = showUserDetailsPanel,
                enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
            ) {
                val user = selectedUser
                if (user != null) {
                    UserManagementDetailsPanel(
                        user = user,
                        onClosePanel = { showUserDetailsPanel = false },
                        onSuccessChangeUser = viewModel::onUsersChanged,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(350.dp)
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(350.dp)
                    )
                }
            }
        }
    }
}
