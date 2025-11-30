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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.github.radlance.autodispatch.delivery.confirmation.presentation.DeliveryConfirmationScreen
import com.github.radlance.autodispatch.delivery.confirmation.presentation.SuccessDeliveryScreen
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryScreen
import com.github.radlance.autodispatch.delivery.details.domain.DeliveryDetailed
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsScreen
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsViewModel
import com.github.radlance.autodispatch.delivery.route.presentation.DeliveryRouteScreen
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val deliveryDetailsViewModel = koinViewModel<DeliveryDetailsViewModel>()

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
                    },
                    deliveryDetailsViewModel = deliveryDetailsViewModel
                )
            }

            composable<DeliveryDetails> {
                val args = it.toRoute<DeliveryDetails>()

                val deliveryId = args.deliveryId
                val deliveryNumber = args.deliveryNumber

                DeliveryDetailsScreen(
                    navigateToDeliveryRoute = {
                        navController.navigate(
                            DeliveryRoute(deliveryId, deliveryNumber)
                        )
                    },
                    navigateUp = navController::navigateUp,
                    deliveryId = deliveryId,
                    deliveryNumber = deliveryNumber,
                    viewModel = deliveryDetailsViewModel
                )
            }

            composable<DeliveryRoute> {
                val args = it.toRoute<DeliveryRoute>()
                DeliveryRouteScreen(
                    deliveryId = args.deliveryId,
                    deliveryNumber = args.deliveryNumber,
                    navigateUp = navController::navigateUp,
                    navigateToDeliveryConfirmation = {
                        navController.navigate(DeliveryConfirmation(args.deliveryId))
                    },
                    viewModel = deliveryDetailsViewModel
                )
            }

            composable<DeliveryConfirmation> {
                val args = it.toRoute<DeliveryConfirmation>()
                DeliveryConfirmationScreen(
                    deliveryId = args.deliveryId,
                    navigateUp = navController::navigateUp,
                    navigateToSuccessDeliveryScreen = { delivery ->
                        val json = Json.encodeToString(delivery)
                        navController.navigate(
                            SuccessDelivery(deliveryDetailedJson = json)
                        ) {
                            popUpTo<DeliveryList>()
                        }
                    },
                    viewModel = deliveryDetailsViewModel
                )
            }

            composable<SuccessDelivery> {
                val args = it.toRoute<SuccessDelivery>()
                val delivery = Json.decodeFromString<DeliveryDetailed>(args.deliveryDetailedJson)
                SuccessDeliveryScreen(
                    delivery = delivery,
                    navigateToDeliveryList = navController::navigateUp
                )
            }
        }

        composable<History> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "History")
            }
        }

        composable<Profile> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Profile")
            }
        }
    }
}