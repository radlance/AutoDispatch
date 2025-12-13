package com.github.radlance.autodispatch.navigation.core

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.driver.core.presentation.DriverScreen
import com.github.radlance.autodispatch.profile.domain.User
import com.github.radlance.autodispatch.request.core.presentation.RequestsScreen

@Composable
fun DrawerNavGraph(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    navigationState: NavigationState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navigationState.navController,
        startDestination = Requests,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) {
        composable<Requests> {
            RequestsScreen(
                loadProfileUiState = loadProfileUiState,
                onReloadProfile = onReloadProfile
            )
        }
        composable<Drivers> {
            DriverScreen(
                loadProfileUiState = loadProfileUiState,
                onReloadProfile = onReloadProfile,
            )
        }
        composable<Vehicles> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = "Cars")
            }
        }
    }
}