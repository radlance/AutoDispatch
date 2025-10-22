package com.github.radlance.autodispatch.controlpanel.presentation

import ShimmerPlaceholder
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import autodispatch.composeapp.generated.resources.dispatcher
import autodispatch.composeapp.generated.resources.retry
import com.github.radlance.autodispatch.common.presentation.AppIconBox
import com.github.radlance.autodispatch.navigation.core.rememberNavigationState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ControlPanelScreen(
    modifier: Modifier = Modifier,
    viewModel: ControlPanelViewModel = koinViewModel()
) {
    val loadProfileUiState by viewModel.loadProfileUiState.collectAsStateWithLifecycle()
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    val items =
        listOf(
            Requests,
            Destinations,
            Cars,
            Drivers,
            Refills,
            Maintenance,
            Reports,
            Notifications
        )
    val navHostController = rememberNavController()
    val navigationState = rememberNavigationState(navHostController)

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
                                            ShimmerPlaceholder(
                                                modifier = Modifier
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
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(Modifier.width(40.dp))
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
        DrawerNavGraph(navigationState = navigationState)
    }
}

private fun abbreviateName(fullName: String): String {
    val words = fullName.trim().split("\\s+".toRegex())
    if (words.isEmpty()) return ""
    val firstWord = words.first()
    val initials =
        words.drop(1).joinToString(" ") { it.firstOrNull()?.uppercaseChar()?.toString() + "." }
    return if (initials.isEmpty()) firstWord else "$firstWord $initials"
}