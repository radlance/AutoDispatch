package com.github.radlance.autodispatch.navigation.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.ok
import autodispatch.composeapp.generated.resources.session_expired
import autodispatch.composeapp.generated.resources.session_expired_description
import com.github.radlance.autodispatch.auth.presentation.SignInScreen
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.controlpanel.presentation.ControlPanelScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun NavGraph(navController: NavHostController) {
    val navigationVieModel = koinViewModel<NavigationViewModel>()

    val authorized = navigationVieModel.authorizedState
    val userRoleId = navigationVieModel.userRoleId
    val sessionExpired by navigationVieModel.sessionExpired.collectAsStateWithLifecycle()
    var showExpiredSessionDialog by rememberSaveable { mutableStateOf(false) }

    if (showExpiredSessionDialog) {
        CustomDialog(
            onDismissRequest = {
                showExpiredSessionDialog = false
            },
            onFinish = {
                navigationVieModel.updateExpirationState()
                navController.navigate(SignIn) {
                    popUpTo<ControlPanel> { inclusive = true }
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(Res.string.session_expired),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            content = {
                Text(text = stringResource(Res.string.session_expired_description))
            },
            buttons = { requestDismiss ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = requestDismiss) {
                    Text(text = stringResource(Res.string.ok))
                }
            }
        )
    }

    LaunchedEffect(sessionExpired) {
        val unauthorizedScreens = listOf(SignIn).map { it.toString() }
        if ((navController.currentDestination?.route?.split(".")
                ?.last() !in unauthorizedScreens) && sessionExpired
        ) {
            showExpiredSessionDialog = true
        }
    }

    val initial = if (authorized) userRoleId?.let {
        ControlPanel(userRoleId = it)
    } ?: SignIn else SignIn

    NavHost(
        navController = navController,
        startDestination = initial,
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
    ) {
        composable<SignIn> {
            SignInScreen(
                navigateToControlPanel = { roleId ->
                    navController.navigate(ControlPanel(userRoleId = roleId))
                }
            )
        }

        composable<ControlPanel> {
            val args = it.toRoute<ControlPanel>()
            ControlPanelScreen(
                userRoleId = args.userRoleId,
                navigateToSignInScreen = {
                    navController.navigate(SignIn) {
                        popUpTo<ControlPanel> { inclusive = true }
                    }
                }
            )
        }
    }
}