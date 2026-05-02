package com.github.radlance.autodispatch.admin.core.presentation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.close_panel
import com.github.radlance.autodispatch.admin.change.preentation.ChangeUserViewModel
import com.github.radlance.autodispatch.admin.core.domain.UserDetailed
import com.github.radlance.autodispatch.admin.core.domain.UserStatus
import com.github.radlance.autodispatch.common.presentation.CustomDialog
import com.github.radlance.autodispatch.common.presentation.DefaultPointerSelectionContainer
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserManagementDetailsPanel(
    user: UserDetailed,
    onClosePanel: () -> Unit,
    onSuccessChangeUser: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangeUserViewModel = koinViewModel()
) {
    val blockUserState by viewModel.blockUserState.collectAsState()
    val scrollState = rememberScrollState()
    rememberCoroutineScope()

    var showBLockDialog by remember { mutableStateOf(false) }

    if (showBLockDialog) {
        val isLoading = blockUserState is FetchResultUiState.Loading
        val error = (blockUserState as? FetchResultUiState.Error)?.error
        val blockText = if (user.status == UserStatus.Blocked) {
            "Разблокировать"
        } else "Заблокировать"
        CustomDialog(
            allowDismiss = !isLoading,
            onDismissRequest = {
                showBLockDialog = false
            },
            onFinish = viewModel::resetBlockState,
            title = {
                val text = if (user.status == UserStatus.Blocked) {
                    "Разблокировка"
                } else "Блокировка"
                Text(
                    text = "$text пользователя",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            content = { requestDismiss ->

                Column(modifier = Modifier.fillMaxWidth()) {
                    error?.let {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Text(
                        buildAnnotatedString {
                            append("Вы уверены, что хотите ${blockText.lowercase()} пользователя ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(user.email)
                            }
                            append("?")
                        }
                    )
                }
                LaunchedEffect(blockUserState) {
                    if (blockUserState is FetchResultUiState.Success) {
                        onSuccessChangeUser()
                        requestDismiss()
                    }
                }
            },
            buttons = { requestDismiss ->
                Spacer(Modifier.weight(1f))
                TextButton(onClick = requestDismiss, enabled = !isLoading) {
                    Text(text = stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(14.dp))
                Button(
                    onClick = {
                        if (user.status == UserStatus.Blocked) {
                            viewModel.unblockUser(user.id)
                        } else viewModel.blockUser(user.id)
                    },
                    enabled = !isLoading
                ) {

                    Text(text = blockText)
                }
            }
        )
    }

    DefaultPointerSelectionContainer {
        Column(modifier = modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Информация о пользователе",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onClosePanel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.close_panel)
                    )
                }
            }
            Box {
                UserDetailsSection(
                    scrollState = scrollState,
                    user = user,
                    onBlockUser = {
                        showBLockDialog = true
                    }
                )
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().offset(x = 3.dp),
                    adapter = rememberScrollbarAdapter(scrollState)
                )
            }
        }
    }
}