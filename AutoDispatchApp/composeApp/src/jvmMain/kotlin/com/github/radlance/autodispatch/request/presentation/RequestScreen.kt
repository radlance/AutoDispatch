package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create_request
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.profile.domain.User
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    loadProfileUiState: FetchResultUiState<User, String>,
    onReloadProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestViewModel = koinViewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    val searchBarState = rememberSearchBarState()
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }

    val requestsUiState by viewModel.loadRequestUiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        requestsUiState.Reduce(
            onLoading = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            },
            onSuccess = { request ->
                val filterOptions =
                    listOf("Все заявки") + request.cargoTypes.map { it.name }

                if (selectedOption.isEmpty()) {
                    selectedOption = filterOptions.first()
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        SearchBar(
                            inputField = {
                                TextField(
                                    value = query,
                                    onValueChange = { query = it },
                                    placeholder = {
                                        Text(
                                            "Поиск по заявкам…",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search Icon"
                                        )
                                    },
                                    trailingIcon = {
                                        if (query.isNotEmpty()) {
                                            IconButton(
                                                onClick = { query = "" }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear search"
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true
                                )
                            },
                            state = searchBarState,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(TextFieldDefaults.MinHeight).weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.height(TextFieldDefaults.MinHeight)
                                    .width(250.dp)
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SearchBarDefaults.colors().containerColor)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple()
                                    ) { }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FilterAlt,
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = selectedOption,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(2f)
                                    )
                                    val trailingIcon = if (expanded) {
                                        Icons.Outlined.ExpandLess
                                    } else Icons.Outlined.ExpandMore
                                    Icon(
                                        imageVector = trailingIcon, contentDescription = null,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                filterOptions.forEach { optionName ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                optionName,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            selectedOption = optionName
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.onGloballyPositioned { cords ->
                                buttonSize = cords.size
                            }
                        ) {
                            Text(text = stringResource(Res.string.create_request))
                        }
                    }
                }
            },
            onError = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.loadRequests()
                                if (loadProfileUiState is FetchResultUiState.Error) {
                                    onReloadProfile()
                                }
                            }
                        ) {
                            Text(text = "Повторить")
                        }
                    }
                }
            }
        )
    }
}