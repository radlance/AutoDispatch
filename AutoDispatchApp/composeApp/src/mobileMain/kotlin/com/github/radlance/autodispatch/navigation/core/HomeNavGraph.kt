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
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryScreen
import com.github.radlance.autodispatch.delivery.core.presentation.DeliveryViewModel
import com.github.radlance.autodispatch.delivery.details.presentation.DeliveryDetailsScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val deliveryViewModel = koinViewModel<DeliveryViewModel>()

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
                    navigateToDeliveryDetails = { navController.navigate(DeliveryDetails(it)) },
                    viewModel = deliveryViewModel
                )
            }

            composable<DeliveryDetails> {
                val args = it.toRoute<DeliveryDetails>()

                DeliveryDetailsScreen(
                    navigateUp = navController::navigateUp,
                    requestNumber = args.requestNumber,
                    viewModel = deliveryViewModel
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