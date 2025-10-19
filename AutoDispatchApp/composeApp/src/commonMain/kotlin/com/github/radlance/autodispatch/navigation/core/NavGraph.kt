package com.github.radlance.autodispatch.navigation.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
expect fun NavGraph(navController: NavHostController)

fun sharedXTransitionIn(
    initial: (fullWidth: Int) -> Int,
    durationMillis: Int = NAVIGATION_TIME,
): EnterTransition {
    val outgoingDuration = (durationMillis * OFFSET_LIMIT).toInt()
    val incomingDuration = durationMillis - outgoingDuration

    return slideInHorizontally(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ), initialOffsetX = initial
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = incomingDuration,
            delayMillis = outgoingDuration,
            easing = LinearOutSlowInEasing
        )
    )
}

fun sharedXTransitionOut(
    target: (fullWidth: Int) -> Int,
    durationMillis: Int = NAVIGATION_TIME,
): ExitTransition {
    val outgoingDuration = (durationMillis * OFFSET_LIMIT).toInt()

    return slideOutHorizontally(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        ), targetOffsetX = target
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = outgoingDuration,
            delayMillis = 0,
            easing = FastOutLinearInEasing
        )
    )
}

private const val NAVIGATION_TIME = 300
const val INITIAL_OFFSET = 0.175f
private const val OFFSET_LIMIT = 0.4f