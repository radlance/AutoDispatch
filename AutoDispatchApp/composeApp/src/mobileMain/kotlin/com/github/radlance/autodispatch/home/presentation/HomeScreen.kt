package com.github.radlance.autodispatch.home.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.radlance.autodispatch.navigation.core.NavigationState
import com.github.radlance.autodispatch.navigation.core.rememberNavigationState

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navigationState = rememberNavigationState(navController)
    Scaffold(
        modifier = modifier,
        bottomBar = {
            HomeBottomBar(navigationState)
        }
    ) { padding ->
        HomeNavGraph(navController = navController, modifier = Modifier.padding(padding))
    }
}

@Composable
fun HomeBottomBar(navigationState: NavigationState) {
    NavigationBar {
        val navBackStackEntry by navigationState.navController.currentBackStackEntryAsState()
        val items = listOf(Requests, History, Profile)
        items.forEach { navigationItem ->
            val isSelected = navBackStackEntry?.destination?.hierarchy?.any {
                it.route == navigationItem::class.qualifiedName
            } ?: false

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navigationState.navigateTo(navigationItem)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) {
                            navigationItem.selectedIcon
                        } else {
                            navigationItem.unselectedIcon
                        },
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = navigationItem.label)
                }
            )
        }
    }
}