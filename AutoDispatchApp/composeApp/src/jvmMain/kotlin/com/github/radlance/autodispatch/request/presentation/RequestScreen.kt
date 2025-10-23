package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.create_request
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
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
                        SearchField(
                            query = query,
                            onQueryChange = { query = it },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        RequestCategories(
                            selectedOption = selectedOption,
                            filterOptions = filterOptions,
                            onOptionSelected = { selectedOption = it },
                        )
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.onGloballyPositioned { cords ->
                                buttonSize = cords.size
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(Res.string.create_request))
                        }
                    }
                }
            },
            onError = {
                ErrorMessage(
                    message = it,
                    onRetry = {
                        viewModel.loadRequests()
                        if (loadProfileUiState is FetchResultUiState.Error) {
                            onReloadProfile()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }
}