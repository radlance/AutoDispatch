package com.github.radlance.autodispatch.request.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    labelText: String? = null,
    isRequired: Boolean = false,
    searchBarColors: SearchBarColors = SearchBarDefaults.colors(),
    placeholderFontSize: TextUnit = TextUnit.Unspecified,
    singleLine: Boolean = true,
    height: Dp = Dp.Unspecified,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(16.dp)
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "borderColorAnimation"
    )

    Column(modifier = modifier) {
        if (labelText != null) {
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp))
        }

        SearchBar(
            colors = searchBarColors,
            inputField = {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = placeholderFontSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it,
                                contentDescription = "Search Icon"
                            )
                        }
                    },
                    trailingIcon = {
                        if (value.isNotEmpty()) {
                            IconButton(
                                onClick = { onValueChange("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = singleLine,
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                )
            },
            state = rememberSearchBarState(),
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = shape
                )
        )
    }
}