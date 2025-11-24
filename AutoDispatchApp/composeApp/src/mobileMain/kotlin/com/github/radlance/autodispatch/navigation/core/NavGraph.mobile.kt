package com.github.radlance.autodispatch.navigation.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.ok
import autodispatch.composeapp.generated.resources.session_expired
import autodispatch.composeapp.generated.resources.session_expired_description
import com.github.radlance.autodispatch.home.presentation.HomeScreen
import com.github.radlance.autodispatch.platform.MapPoint
import com.github.radlance.autodispatch.platform.MapRouteDialog
import com.github.radlance.autodispatch.platform.createLocationPermissionController
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openAppSettings
import com.github.radlance.autodispatch.platform.openDialer
import com.github.radlance.autodispatch.reuqest.core.domain.Point
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
            var selectedAddress by remember { mutableStateOf<String?>(null) }
            var selectedPoint by remember { mutableStateOf<Point?>(null) }
            val context = getPlatformContext()
            val permissionController = createLocationPermissionController {

            }

            selectedAddress?.let {
                MapPoint(
                    address = it,
                    onDismiss = { selectedAddress = null }
                )
            }

            selectedPoint?.let {
                MapRouteDialog(
                    lat = it.lat,
                    lon = it.lon,
                    onDismiss = { selectedPoint = null }
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { openDialer(phoneNumber = "+79999999999", context) }) {
                    Text(text = "Набрать номер")
                }
                Button(onClick = { selectedAddress = "59.91166445623312, 30.318145751953125" }) {
                    Text(text = "Открыть точку")
                }
                Button(onClick = {
                    selectedPoint = Point(
                        address = "59.91166445623312, 30.318145751953125",
                        lat = 59.91166445623312,
                        lon = 30.318145751953125
                    )
                }) {
                    Text(text = "Построить маршрут")
                }

                Button(onClick = { openAppSettings(context) }) {
                    Text(text = "Открыть настройки")
                }

                Button(onClick = { permissionController.askPermission() }) {
                    Text(text = "запросить геолокацию")
                }
            }
        }

        composable<Home> {
            HomeScreen()
        }
    }
}