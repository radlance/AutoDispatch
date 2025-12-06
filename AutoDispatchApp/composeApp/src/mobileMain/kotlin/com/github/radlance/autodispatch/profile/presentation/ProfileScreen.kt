package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.radlance.autodispatch.common.presentation.ErrorMessage
import com.github.radlance.autodispatch.common.presentation.FetchResultUiState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

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
                    // TODO shimmer
                },
                onSuccess = { profileDetails ->
                    DriverProfile(profileDetails = profileDetails)
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

// TODO добавить грузоподьемность у автомобиля
// TODO обработать отсутствие аввтомобиля
// TODO? сделать иконки у доставок
/*
* item {
        StatTile(
            title = "Активные",
            value = stats.activeCount,
            icon = Icons.Outlined.LocalShipping
        )
    }

    item {
        StatTile(
            title = "На проверке",
            value = stats.onCheckCount,
            icon = Icons.Outlined.HourglassTop
        )
    }

    item {
        StatTile(
            title = "Завершённые",
            value = stats.completedCount,
            icon = Icons.Outlined.CheckCircle
        )
    }

    item {
        StatTile(
            title = "Отменённые",
            value = stats.canceledCount,
            icon = Icons.Outlined.Cancel
        )
    }

    item {
        StatTile(
            title = "Отклонённые",
            value = stats.rejectedCount,
            icon = Icons.Outlined.Block
        )
    }
* */