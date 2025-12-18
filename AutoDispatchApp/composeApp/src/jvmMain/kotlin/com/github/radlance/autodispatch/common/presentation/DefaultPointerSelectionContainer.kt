package com.github.radlance.autodispatch.common.presentation

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

@Composable
fun DefaultPointerSelectionContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SelectionContainer(
        modifier = modifier.pointerHoverIcon(
            PointerIcon.Default,
            overrideDescendants = true
        ),
        content = content
    )
}