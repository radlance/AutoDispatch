package com.github.radlance.autodispatch.navigation.core

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.radlance.autodispatch.admin.core.presentation.UserManagementScreen
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User

@Composable
fun AdminNavGraph(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    navigationState: NavigationState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navigationState.navController,
        startDestination = Users,
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
        composable<Users> {
            UserManagementScreen(
                loadProfileUiState = loadProfileUiState,
                onReloadProfile = onReloadProfile
            )
        }
    }
}