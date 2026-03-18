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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (requestDismiss: () -> Unit) -> Unit,
    content: @Composable (requestDismiss: () -> Unit) -> Unit,
    buttons: @Composable RowScope.(requestDismiss: () -> Unit) -> Unit,
    onFinish: () -> Unit = {},
    allowDismiss: Boolean = true,
    modifier: Modifier = Modifier,
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
            properties = PopupProperties(
                focusable = true
            )
        ) {
            AppBackHandler(enabled = (visibleState.currentState || visibleState.targetState) && allowDismiss) {
                if (!dismissRequested) {
                    dismissRequested = true
                }
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
                    Surface(
                        modifier = modifier
                            .widthIn(min = 280.dp, max = 560.dp),
                        shape = AlertDialogDefaults.shape,
                        color = AlertDialogDefaults.containerColor,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            title { attemptDismiss() }

                            Spacer(Modifier.height(16.dp))

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                content { attemptDismiss() }
                            }

                            Spacer(Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                buttons { attemptDismiss() }
                            }
                        }
                    }
                }
            }
        }
    }
}
