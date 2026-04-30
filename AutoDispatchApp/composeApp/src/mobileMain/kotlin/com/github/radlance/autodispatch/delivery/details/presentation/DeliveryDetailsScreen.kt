package com.github.radlance.autodispatch.delivery.details.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.delivery
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.delivery.domain.RequestError
import com.github.radlance.autodispatch.request.core.domain.DocumentType
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailsScreen(
    deliveryId: Int,
    deliveryNumber: String,
    navigateToDeliveryRoute: () -> Unit,
    navigateToDeliveryConfirmation: (docType: DocumentType) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeliveryDetailsViewModel = koinViewModel()
) {
    val requestState by viewModel.deliveryState.collectAsStateWithLifecycle()
    val acceptDeliveryState by viewModel.acceptDeliveryState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            append("${stringResource(Res.string.delivery)} ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                val currentState = requestState
                                if (currentState is FetchResultUiState.Success) {
                                    append(currentState.data.requestNumber)
                                } else append(deliveryNumber)
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = requestState is FetchResultUiState.Loading,
            onRefresh = { viewModel.fetchDeliveryDetails(deliveryId) }
        ) {
            requestState.Reduce(
                onLoading = {
                    DeliveryDetailsShimmer()
                },
                onSuccess = { delivery ->
                    DeliveryDetails(
                        scrollState = scrollState,
                        delivery = delivery,
                        onContinueDeliveryClick = navigateToDeliveryRoute,
                        onRetakeDocumentsClick = {
                            val type = if (delivery.actualUnloadingAt == null) {
                                DocumentType.SHIPPING
                            } else {
                                DocumentType.ACCEPTANCE
                            }
                            navigateToDeliveryConfirmation(type)
                        },
                        onAcceptClick = { viewModel.acceptDelivery(delivery.id) },
                        onCloseError = viewModel::resetAcceptState,
                        navigateUp = navigateUp,
                        fetchDeliveryDetails = {
                            viewModel.fetchDeliveryDetails(deliveryId)
                            viewModel.resetAcceptState()
                        },
                        acceptDeliveryState = acceptDeliveryState
                    )
                },
                onError = {
                    if (it is RequestError.BaseError) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ErrorMessage(
                                message = it.message,
                                onRetry = { viewModel.fetchDeliveryDetails(deliveryId) }
                            )
                        }
                    } else {
                        val onDismiss: () -> Unit = {
                            viewModel.resetAcceptState()
                            navigateUp()
                        }
                        AlertDialog(
                            onDismissRequest = onDismiss,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null
                                )
                            },
                            title = {
                                Text(text = "Ошибка")
                            },
                            text = {
                                Text(text = it.message)
                            },
                            dismissButton = {},
                            confirmButton = {
                                Button(
                                    onClick = onDismiss
                                ) {
                                    Text(text = "ОК")
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}