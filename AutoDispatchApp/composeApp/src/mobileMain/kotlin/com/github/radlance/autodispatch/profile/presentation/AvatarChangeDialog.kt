package com.github.radlance.autodispatch.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.radlance.autodispatch.common.presentation.LoadableImage

@Composable
fun AvatarChangeDialog(
    driverFullName: String,
    onDismissRequest: () -> Unit,
    onUploadImageClick: () -> Unit,
    onDeleteImageClick: () -> Unit,
    bmp: ImageBitmap?,
    avatarUrl: String?,
    lastImageRetryAttempt: Long,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Аватар профиля",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(150.dp)
                        .clip(CircleShape)
                        .background(CardDefaults.cardColors().containerColor)
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
                        avatarUrl?.let { documentUrl ->
                            LoadableImage(
                                documentUrl = documentUrl,
                                onRetry = onRetry,
                                lastRetryAttempt = lastImageRetryAttempt,
                                modifier = Modifier.fillMaxSize(),
                                showLoading = false
                            )
                        } ?: Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(150.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text(text = avatarInitials(driverFullName), fontSize = 36.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        onUploadImageClick()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(text = "Загрузить фото")
                }
                if (bmp != null || avatarUrl != null) {
                    FilledIconButton(
                        onClick = {
                            onDeleteImageClick()
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    )
}

fun avatarInitials(input: String): String {
    if (input.isBlank()) return "-"

    val words = input
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    if (words.isEmpty()) return "-"

    return words
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "-" }
}
