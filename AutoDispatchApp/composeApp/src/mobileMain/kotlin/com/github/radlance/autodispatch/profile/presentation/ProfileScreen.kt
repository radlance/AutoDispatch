package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.cancel
import autodispatch.composeapp.generated.resources.exit
import autodispatch.composeapp.generated.resources.you_want_to_logout
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import com.github.radlance.autodispatch.platform.getPlatformContext
import com.github.radlance.autodispatch.platform.openAppSettings
import com.github.radlance.autodispatch.platform.rememberGalleryLauncher
import com.github.radlance.autodispatch.platform.rememberPhotoPermissionController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ProfileScreen(
    navigateToSignInScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var lastImageRetryAttempt by rememberSaveable { mutableStateOf(0L) }
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val avatar by viewModel.avatar
    val controller = rememberPhotoPermissionController {
        hasPermission = it
    }
    val galleryLauncher = rememberGalleryLauncher { picture ->
        picture?.let {
            viewModel.uploadProfileImage(it)
        }
    }

    val context = getPlatformContext()

    if (hasPermission == false) {
        AlertDialog(
            onDismissRequest = { hasPermission = null },
            icon = {
                Icon(
                    Icons.Outlined.PhotoLibrary,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Доступ к фото") },
            text = {
                Text(
                    "Разрешите доступ к фото для корректной работы приложения.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    openAppSettings(context)
                    hasPermission = null
                }) { Text("Настройки") }
            },
            dismissButton = {
                TextButton(onClick = { hasPermission = null }) { Text("Отмена") }
            }
        )
    }

    LaunchedEffect(Unit) {
        if (controller.hasPermission()) {
            hasPermission = true
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text(text = stringResource(Res.string.exit))
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(Res.string.you_want_to_logout))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = stringResource(Res.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        navigateToSignInScreen()
                    }
                ) {
                    Text(text = stringResource(Res.string.exit))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Профиль")
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = profileState is FetchResultUiState.Loading,
            onRefresh = viewModel::loadProfile
        ) {
            profileState.Reduce(
                onLoading = {
                    DriverProfileShimmer()
                },
                onSuccess = { profileDetails ->
                    LaunchedEffect(profileDetails.avatarUrl) {
                        viewModel.clearTemporaryAvatar()
                    }
                    val bmp = remember(avatar) {
                        avatar?.decodeToImageBitmap()
                    }

                    DriverProfile(
                        profileDetails = profileDetails,
                        lastImageRetryAttempt = lastImageRetryAttempt,
                        onRetry = {
                            lastImageRetryAttempt = Clock.System.now().toEpochMilliseconds()
                        },
                        bmp = bmp,
                        onProfilePictureClick = {
                            if (controller.hasPermission()) {
                                galleryLauncher.pick()
                            } else controller.askPermission()
                        },
                        onLogoutClick = { showLogoutDialog = true }
                    )
                },
                onError = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(message = it, onRetry = viewModel::loadProfile)
                    }
                }
            )
        }
    }
}