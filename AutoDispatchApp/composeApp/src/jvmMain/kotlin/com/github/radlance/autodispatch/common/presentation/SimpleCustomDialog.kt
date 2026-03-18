package com.github.radlance.autodispatch.common.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleCustomDialog(
    onDismissRequest: () -> Unit,
    onFinish: () -> Unit = {},
    allowDismiss: Boolean = true,
    properties: PopupProperties = PopupProperties(
        focusable = true
    ),
    content: @Composable (requestDismiss: () -> Unit) -> Unit
) {
    val visibleState = remember { MutableTransitionState(false) }
    var dismissRequested by remember { mutableStateOf(false) }

    val attemptDismiss = {
        if (allowDismiss) {
            dismissRequested = true
        }
    }

    DisableSelection {
        Popup(
            onDismissRequest = attemptDismiss,
            properties = properties
        ) {
            AppBackHandler(enabled = (visibleState.currentState || visibleState.targetState) && allowDismiss) {
                attemptDismiss()
            }

            LaunchedEffect(Unit) {
                visibleState.targetState = true
            }

            LaunchedEffect(dismissRequested) {
                if (dismissRequested) {
                    visibleState.targetState = false
                }
            }

            LaunchedEffect(Unit) {
                snapshotFlow {
                    visibleState.isIdle && !visibleState.currentState
                }.collect { ready ->
                    if (ready && dismissRequested) {
                        onDismissRequest()
                        onFinish()
                        dismissRequested = false
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessHigh)),
                    exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                attemptDismiss()
                            }
                    )
                }

                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn() + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = fadeOut() + scaleOut(
                        targetScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                ) {
                    Box {
                        content(attemptDismiss)
                    }
                }
            }
        }
    }
}
