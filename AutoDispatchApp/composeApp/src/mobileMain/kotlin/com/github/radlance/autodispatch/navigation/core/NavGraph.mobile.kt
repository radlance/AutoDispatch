package com.github.radlance.autodispatch.navigation.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.ok
import autodispatch.composeapp.generated.resources.session_expired
import autodispatch.composeapp.generated.resources.session_expired_description
import com.github.radlance.autodispatch.auth.presentation.SignInScreen
import com.github.radlance.autodispatch.home.presentation.HomeScreen
import com.github.radlance.autodispatch.splash.presentation.SplashScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun NavGraph(navController: NavHostController) {
    val navigationVieModel = koinViewModel<NavigationViewModel>()
    val authorized by navigationVieModel.authorizedState.collectAsStateWithLifecycle()
    val sessionExpired by navigationVieModel.sessionExpired.collectAsStateWithLifecycle()
    var showExpiredSessionDialog by rememberSaveable { mutableStateOf(false) }

    if (showExpiredSessionDialog) {

        val onDismissRequest: () -> Unit = {
            showExpiredSessionDialog = false
            navController.navigate(SignIn) {
                popUpTo<Home> { inclusive = true }
            }
        }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(Res.string.ok))
                }
            },
            icon = {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null)
            },
            title = {
                Text(text = stringResource(Res.string.session_expired))
            },
            text = {
                Text(text = stringResource(Res.string.session_expired_description))
            }
        )
    }

    LaunchedEffect(sessionExpired) {
        val unauthorizedScreens = listOf(Splash, SignIn).map { it.toString() }
        if ((navController.currentDestination?.route?.split(".")
                ?.last() !in unauthorizedScreens) && sessionExpired
        ) {
            showExpiredSessionDialog = true
        }
    }

    NavHost(
        navController = navController,
        enterTransition = {
            sharedXTransitionIn(initial = { (it * INITIAL_OFFSET).toInt() })
        },
        exitTransition = {
            sharedXTransitionOut(target = { -(it * INITIAL_OFFSET).toInt() })
        },
        popEnterTransition = {
            sharedXTransitionIn(initial = { -(it * INITIAL_OFFSET).toInt() })
        },
        popExitTransition = {
            sharedXTransitionOut(target = { -(it * INITIAL_OFFSET).toInt() })
        },
        startDestination = Splash
    ) {
        composable<Splash> {
            SplashScreen(
                onDelayFinish = {
                    val screen = if (authorized) Home else SignIn

                    navController.navigate(screen) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<SignIn> {
            SignInScreen(
                navigateToHomeScreen = {
                    navController.navigate(Home) {
                        popUpTo<SignIn> { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen()
        }
    }
}