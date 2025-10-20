package com.github.radlance.autodispatch.navigation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationState(val navController: NavHostController) {

    fun <T : Any> navigateTo(route: T) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }
}

@Composable
fun rememberNavigationState(navController: NavHostController): NavigationState {
    return remember { NavigationState(navController) }
}