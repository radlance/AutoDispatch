package com.github.radlance.autodispatch.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.radlance.autodispatch.presentation.auth.SignInScreen

@Composable
actual fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SignIn,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) }
    ) {
        composable<SignIn> {
            SignInScreen(onSignInClick = { navController.navigate(ControlPanel) })
        }

        composable<ControlPanel> {
            ControlPanelScreen(navController)
        }
    }
}

@Composable
private fun ControlPanelScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}