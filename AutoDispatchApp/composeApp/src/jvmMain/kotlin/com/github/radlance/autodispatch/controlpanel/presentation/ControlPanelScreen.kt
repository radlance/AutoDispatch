package com.github.radlance.autodispatch.controlpanel.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.dispatcher
import autodispatch.composeapp.generated.resources.exit
import autodispatch.composeapp.generated.resources.retry
import autodispatch.composeapp.generated.resources.you_want_to_logout
import com.github.radlance.autodispatch.auth.presentation.AppIconBox
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.common.presentation.shimmerBackground
import com.github.radlance.autodispatch.common.utils.abbreviateName
import com.github.radlance.autodispatch.navigation.core.DrawerNavGraph
import com.github.radlance.autodispatch.navigation.core.Drivers
import com.github.radlance.autodispatch.navigation.core.Requests
import com.github.radlance.autodispatch.navigation.core.Statistic
import com.github.radlance.autodispatch.navigation.core.Vehicles
import com.github.radlance.autodispatch.navigation.core.rememberNavigationState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ControlPanelScreen(
    navigateToSignInScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ControlPanelViewModel = koinViewModel()
) {
    val loadProfileUiState by viewModel.loadProfileUiState.collectAsState()
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    val items =
        listOf(
            Requests,
            Drivers,
            Vehicles,
            Statistic
        )
    val navHostController = rememberNavController()
    val navigationState = rememberNavigationState(navHostController)
    var showLogoutDialog by remember { mutableStateOf(false) }
    var shouldLogout by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        CustomDialog(
            modifier = modifier,
            onDismissRequest = {
                showLogoutDialog = false
            },
            onFinish = {
                if (shouldLogout) {
                    viewModel.logout()
                    navigateToSignInScreen()
                    shouldLogout = false
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.exit),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(Res.string.you_want_to_logout))
                }
            },
            buttons = { requestDismiss ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = requestDismiss) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(12.dp))
                TextButton(
                    onClick = {
                        shouldLogout = true
                        requestDismiss()
                    }
                ) {
                    Text(text = stringResource(Res.string.exit))
                }
            }
        )
    }

    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier.width(240.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                ) {
                    AppIconBox(
                        boxSize = 50.dp,
                        clipAngle = 10.dp,
                        iconSize = 35.dp
                    )

                    Spacer(Modifier.width(24.dp))
                    Column {
                        Text(text = stringResource(Res.string.auto_request), fontSize = 20.sp)
                        Text(
                            text = stringResource(Res.string.dispatcher),
                            fontSize = 14.sp,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Box(modifier = Modifier.weight(1f)) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier.fillMaxHeight().verticalScroll(scrollState)
                    ) {
                        items.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(stringResource(item.titleRes)) },
                                selected = index == selectedItem,
                                onClick = {
                                    selectedItem = index
                                    navigationState.navigateTo(item)
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.height(18.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp),
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }

                            Spacer(Modifier.width(18.dp))
                            loadProfileUiState.Reduce(
                                onLoading = {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .shimmerBackground()
                                                    .height(20.dp)
                                                    .width(120.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(40.dp))
                                    }
                                },
                                onSuccess = { user ->
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = abbreviateName(user.fullName),
                                            fontSize = 14.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { showLogoutDialog = true },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Default.Logout,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                },
                                onError = { message ->
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = message,
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 13.sp,
                                                softWrap = true,
                                                lineHeight = 15.sp
                                            )
                                        }
                                        IconButton(
                                            modifier = Modifier.size(40.dp),
                                            onClick = viewModel::loadProfile
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Refresh,
                                                contentDescription = stringResource(Res.string.retry),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                            .padding(end = 2.dp),
                        adapter = rememberScrollbarAdapter(scrollState)
                    )
                }
            }
        }
    ) {
        DrawerNavGraph(
            navigationState = navigationState,
            loadProfileUiState = loadProfileUiState,
            onReloadProfile = viewModel::loadProfile
        )
    }
}