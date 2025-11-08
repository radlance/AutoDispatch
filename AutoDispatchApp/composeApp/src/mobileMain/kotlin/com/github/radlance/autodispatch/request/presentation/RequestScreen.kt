package com.github.radlance.autodispatch.request.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import autodispatch.composeapp.generated.resources.Res
import autodispatch.composeapp.generated.resources.auto_request
import com.github.radlance.autodispatch.auth.presentation.AppIconBox
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    modifier: Modifier = Modifier,
    requestViewModel: RequestViewModel = koinViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val requests by requestViewModel.requestsState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconBox(
                            boxSize = 38.dp,
                            clipAngle = 10.dp,
                            iconSize = 33.dp
                        )

                        Spacer(Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = stringResource(Res.string.auto_request),
                                fontSize = 14.sp,
                                modifier = Modifier.offset(y = 3.dp)
                            )
                            Text(
                                text = "Водитель",
                                fontSize = 14.sp,
                                modifier = Modifier.alpha(0.5f).offset(y = (-3).dp)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsNone,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = TopAppBarDefaults.topAppBarColors().containerColor
                )
            )
        }
    ) { innerPadding ->
        requests.Reduce(
            onSuccess = {
                Text(it.toString())
            },
            onError = {
                Text("Error")
            }
        )
    }
}