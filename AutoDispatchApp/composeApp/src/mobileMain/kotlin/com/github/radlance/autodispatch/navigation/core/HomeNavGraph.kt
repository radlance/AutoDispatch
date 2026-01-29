package com.github.radlance.autodispatch.navigation.core

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.github.radlance.autodispatch.delivery.confirmation.presentation.DeliveryConfirmationScreen
import com.github.radlance.autodispatch.delivery.confirmation.presentation.SuccessUploadScreen
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryScreen
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsScreen
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import com.github.radlance.autodispatch.delivery.route.presentation.DeliveryRouteScreen
import com.github.radlance.autodispatch.history.presentation.DeliveryHistoryScreen
import com.github.radlance.autodispatch.profile.presentation.ProfileScreen
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeNavGraph(
    navigateToSignInScreen: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val deliveryDetailsViewModel = koinViewModel<DeliveryDetailsViewModel>(
        parameters = { parametersOf(0) }
    )
    val deepLinkManager = koinInject<DeepLinkManager>()
    val pendingId by deepLinkManager.pendingRoute.collectAsStateWithLifecycle()

    LaunchedEffect(pendingId) {
        pendingId?.let { id ->
            navController.navigate(DeliveryDetails(deliveryId = id, deliveryNumber = null))
            deepLinkManager.consume()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Deliveries,
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
        navigation<Deliveries>(startDestination = DeliveryList) {
            composable<DeliveryList> {
                DeliveryScreen(
                    navigateToDeliveryDetails = { id, number ->
                        navController.navigate(
                            DeliveryDetails(id, number)
                        )
                    },
                    navigateToDeliveryRoute = { id, number ->
                        navController.navigate(DeliveryRoute(id, number))
                    }
                )
            }

            composable<DeliveryDetails> {
                val args = it.toRoute<DeliveryDetails>()

                val deliveryId = args.deliveryId

                DeliveryDetailsScreen(
                    navigateToDeliveryRoute = {
                        navController.navigate(
                            DeliveryRoute(deliveryId, deliveryNumber = args.deliveryNumber ?: "№${args.deliveryId}")
                        )
                    },
                    navigateUp = navController::navigateUp,
                    deliveryId = deliveryId,
                    deliveryNumber = args.deliveryNumber ?: "№${args.deliveryId}",
                    navigateToDeliveryConfirmation = {
                        navController.navigate(
                            DeliveryConfirmation(deliveryId = deliveryId, retake = true)
                        )
                    }
                )
            }

            composable<DeliveryRoute> {
                val args = it.toRoute<DeliveryRoute>()
                val deliveryId = args.deliveryId
                DeliveryRouteScreen(
                    deliveryId = deliveryId,
                    deliveryNumber = args.deliveryNumber,
                    navigateUp = navController::navigateUp,
                    navigateToDeliveryConfirmation = {
                        navController.navigate(
                            DeliveryConfirmation(deliveryId = deliveryId, retake = false)
                        )
                    },
                    viewModel = deliveryDetailsViewModel
                )
            }

            composable<DeliveryConfirmation> {
                val args = it.toRoute<DeliveryConfirmation>()
                DeliveryConfirmationScreen(
                    deliveryId = args.deliveryId,
                    retake = args.retake,
                    navigateUp = navController::navigateUp,
                    navigateToSuccessDeliveryScreen = { delivery ->
                        val json = Json.encodeToString(delivery)
                        navController.navigate(
                            SuccessUpload(deliveryDetailedJson = json)
                        ) {
                            popUpTo<DeliveryList>()
                        }
                    },
                    viewModel = deliveryDetailsViewModel
                )
            }

            composable<SuccessUpload> {
                val args = it.toRoute<SuccessUpload>()
                val delivery = Json.decodeFromString<DeliveryDetailed>(args.deliveryDetailedJson)
                SuccessUploadScreen(
                    delivery = delivery,
                    navigateToDeliveryList = navController::navigateUp
                )
            }
        }

        navigation<History>(startDestination = HistoryList) {
            composable<HistoryList> {
                DeliveryHistoryScreen(
                    navigateToDeliveryDetails = { id, number ->
                        navController.navigate(
                            HistoryDetails(id, number)
                        )
                    },
                    navigateToDeliveryRoute = { _, _ -> },
                    deliveryDetailsViewModel = deliveryDetailsViewModel
                )
            }

            composable<HistoryDetails> {
                val args = it.toRoute<DeliveryDetails>()

                val deliveryId = args.deliveryId

                DeliveryDetailsScreen(
                    navigateToDeliveryRoute = {},
                    navigateUp = navController::navigateUp,
                    deliveryId = deliveryId,
                    deliveryNumber = args.deliveryNumber ?: "№${args.deliveryId}",
                    navigateToDeliveryConfirmation = {},
                    viewModel = deliveryDetailsViewModel
                )
            }
        }

        composable<Profile> {
            ProfileScreen(navigateToSignInScreen = navigateToSignInScreen)
        }
    }
}