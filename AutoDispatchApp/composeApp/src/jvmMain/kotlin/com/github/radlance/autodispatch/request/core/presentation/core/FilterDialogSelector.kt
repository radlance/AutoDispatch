package com.github.radlance.autodispatch.request.core.presentation.core

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.all
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.done
import autodispatch.composeapp.generated.resources.no_results_generic
import autodispatch.composeapp.generated.resources.search
import autodispatch.composeapp.generated.resources.select_all
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterDialogSelector(
    title: String,
    options: List<String>,
    selected: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    AssistChip(
        onClick = { showDialog = true },
        label = {
            val text = when {
                selected.isEmpty() || (selected.size == options.size) -> "$title: ${
                    stringResource(
                        Res.string.all
                    )
                }"

                selected.size == 1 -> "$title: ${selected.first()}"
                else -> "$title: ${selected.size}"
            }
            Text(text, fontSize = 14.sp)
        }
    )

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = options,
            initiallySelected = selected.ifEmpty { options },
            onConfirm = {
                onSelectionChanged(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    initiallySelected: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelection by remember { mutableStateOf(initiallySelected) }
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options
        else options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    val screenHeight = LocalWindowInfo.current.containerSize.height
    val maxDialogHeight = screenHeight * 0.6f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(max = maxDialogHeight.dp)) {
                Column {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(Res.string.search)) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .padding(vertical = 4.dp)
                        ) {
                            val masterState = when {
                                tempSelection.isEmpty() -> ToggleableState.Off
                                tempSelection.size == options.size -> ToggleableState.On
                                else -> ToggleableState.Indeterminate
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempSelection = when (masterState) {
                                            ToggleableState.On -> emptyList()
                                            else -> options.toList()
                                        }
                                    }
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                            ) {
                                TriStateCheckbox(
                                    state = masterState,
                                    onClick = {
                                        tempSelection = when (masterState) {
                                            ToggleableState.On -> emptyList()
                                            else -> options.toList()
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(Res.string.select_all),
                                    fontSize = 16.sp,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            HorizontalDivider(thickness = 1.dp)

                            if (filteredOptions.isEmpty()) {
                                Text(
                                    stringResource(Res.string.no_results_generic),
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                filteredOptions.forEach { option ->
                                    val isChecked = option in tempSelection
                                    Box(modifier = Modifier.clickable {
                                        tempSelection = if (isChecked) {
                                            tempSelection - option
                                        } else {
                                            tempSelection + option
                                        }
                                    }) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp, horizontal = 4.dp)
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    tempSelection = if (checked) {
                                                        tempSelection + option
                                                    } else {
                                                        tempSelection - option
                                                    }
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                option,
                                                fontSize = 16.sp,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState),
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .offset(x = 10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tempSelection) },
                enabled = tempSelection.isNotEmpty()
            ) {
                Text(stringResource(Res.string.done), style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel), style = MaterialTheme.typography.labelLarge)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}