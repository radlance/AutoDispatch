package com.github.radlance.autodispatch.request.change.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CustomTextFieldWithDropdown(
    labelText: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suggestions: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }

    val shape = RoundedCornerShape(16.dp)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth().animateContentSize()) {
        Text(
            text = buildAnnotatedString {
                append(labelText)
                if (isRequired) {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                        append(" *")
                    }
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
            TextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                    selectedIndex = -1
                },
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = {
                            onValueChange("")
                            expanded = false
                            selectedIndex = -1
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) {
                            expanded = suggestions.isNotEmpty()
                        } else {
                            coroutineScope.launch {
                                delay(100)
                                expanded = false
                            }
                        }
                    }
                    .border(
                        width = if (isFocused) 1.dp else 0.dp,
                        color = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = shape
                    ),
                singleLine = true,
                shape = shape
            )

            if (expanded && suggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                        .padding(vertical = 14.dp)
                ) {
                    suggestions.forEachIndexed { index, suggestion ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = suggestion,
                                    color = if (index == selectedIndex)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                onSuggestionSelected(suggestion)
                                onValueChange(suggestion)
                                expanded = false
                                selectedIndex = -1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (index == selectedIndex)
                                        MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.1f)
                                    else Color.Transparent
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) {
                                    onSuggestionSelected(suggestion)
                                    onValueChange(suggestion)
                                    expanded = false
                                    selectedIndex = -1
                                }
                        )
                    }
                }
            }
        }
    }
}
