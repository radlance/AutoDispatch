package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.InfoRow
import com.github.radlance.autodispatch.common.presentation.LoadableImage
import com.github.radlance.autodispatch.common.presentation.SectionHeader
import com.github.radlance.autodispatch.common.utils.avatarInitials
import com.github.radlance.autodispatch.profile.domain.ProfileDetails
import com.github.radlance.autodispatch.uikit.vector.AppIcon
import com.github.radlance.autodispatch.uikit.vector.DocumentIcon
import com.github.radlance.autodispatch.uikit.vector.SearchInsightsIcon
import com.github.radlance.autodispatch.uikit.vector.WeightIcon

@Composable
fun DriverProfile(
    profileDetails: ProfileDetails,
    bmp: ImageBitmap?,
    lastImageRetryAttempt: Long,
    onRetry: () -> Unit,
    onProfilePictureClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                        .background(CardDefaults.cardColors().containerColor)
                        .clickable { onProfilePictureClick() }
                ) {
                    bmp?.let {
                        val bmpPainter = remember(bmp) { BitmapPainter(bmp) }
                        Image(
                            painter = bmpPainter,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: run {
                        profileDetails.avatarUrl?.let { avatarUrl ->
                            LoadableImage(
                                documentUrl = avatarUrl,
                                onRetry = onRetry,
                                lastRetryAttempt = lastImageRetryAttempt,
                                onImageSelected = { onProfilePictureClick() },
                                modifier = Modifier.fillMaxSize(),
                                showLoading = false
                            )
                        } ?: Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text(text = avatarInitials(profileDetails.fullName))
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = profileDetails.fullName)
                    SelectionContainer {
                        Text(
                            text = profileDetails.phoneNumber,
                            fontSize = 12.sp,
                            modifier = Modifier.alpha(0.7f)
                        )
                    }
                }
            }
        }

        item {

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text(text = "Статистика доставок")
                Spacer(Modifier.height(8.dp))

                val stats = profileDetails.deliveriesStats
                val isEmpty = stats.activeCount +
                        stats.onCheckCount +
                        stats.completedCount +
                        stats.canceledCount +
                        stats.rejectedCount == 0

                if (isEmpty) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = SearchInsightsIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp).alpha(0.7f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Данных пока нет",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.alpha(0.9f)
                            )
                        }
                    }
                } else {
                    val items = listOf(
                        "Активные" to stats.activeCount,
                        "На проверке" to stats.onCheckCount,
                        "Завершённые" to stats.completedCount,
                        "Отменённые" to stats.canceledCount,
                        "Отклонённые" to stats.rejectedCount,
                    )

                    val visibleItems = items.filter { it.second > 0 }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = 400.dp),
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        visibleItems.forEachIndexed { index, (title, value) ->

                            val isLast = index == visibleItems.lastIndex
                            val isOddCount = visibleItems.size % 2 == 1

                            val span = if (isLast && isOddCount) 2 else 1

                            item(span = { GridItemSpan(span) }) {
                                StatTile(title, value)
                            }
                        }
                    }
                }
            }
        }

        item {
            val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer
            Card {
                SectionHeader(
                    text = "Мой атомобиль",
                    icon = AppIcon,
                    color = onPrimaryContainer,
                    backgroundColor = primaryContainer
                )
                profileDetails.vehicle?.let { vehicle ->
                    SelectionContainer {
                        InfoRow(
                            title = vehicle.model,
                            subtitle = "Модель",
                            icon = AppIcon,
                            iconTint = onPrimaryContainer,
                            iconBackgroundColor = primaryContainer,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp))
                    SelectionContainer {
                        InfoRow(
                            title = vehicle.licensePlate,
                            subtitle = "Гос. номер",
                            icon = DocumentIcon,
                            iconTint = onPrimaryContainer,
                            iconBackgroundColor = primaryContainer,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp))
                    SelectionContainer {
                        InfoRow(
                            title = "${vehicle.payloadCapacity} кг",
                            subtitle = "Грузоподъёмность",
                            icon = WeightIcon,
                            iconTint = onPrimaryContainer,
                            iconBackgroundColor = primaryContainer,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                } ?: Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp).alpha(0.7f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Автомобиль не назначен",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alpha(0.9f)
                    )
                    Text(
                        text = "Обратитесь к диспетчеру для назначения автомобиля",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alpha(0.9f)
                    )
                }
            }
        }

        item {
            Button(onClick = onLogoutClick, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text(text = "Выйти из аккаунта")
            }
        }
    }
}


@Composable
private fun StatTile(
    title: String,
    value: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}