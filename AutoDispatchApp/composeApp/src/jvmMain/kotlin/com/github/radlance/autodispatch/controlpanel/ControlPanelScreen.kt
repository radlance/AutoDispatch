package com.github.radlance.autodispatch.controlpanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import autodispatch.composeapp.generated.resources.dispatcher
import com.github.radlance.autodispatch.common.presentation.AppIconBox
import com.github.radlance.autodispatch.navigation.core.rememberNavigationState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ControlPanelScreen(modifier: Modifier = Modifier) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    val items =
        listOf(
            Applications,
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
            }
        }
    ) {
        DrawerNavGraph(navigationState = navigationState)
    }
}