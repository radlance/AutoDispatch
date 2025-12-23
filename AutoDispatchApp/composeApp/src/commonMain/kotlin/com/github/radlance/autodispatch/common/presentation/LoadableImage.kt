package com.github.radlance.autodispatch.common.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size

@Composable
fun LoadableImage(
    documentUrl: String,
    onRetry: () -> Unit,
    onImageSelected: (String) -> Unit,
    lastRetryAttempt: Long,
    modifier: Modifier = Modifier,
    showLoading: Boolean = true
) {
    val context = LocalPlatformContext.current
    val request = remember(documentUrl, lastRetryAttempt) {
        val uniqueDataUrl = if (lastRetryAttempt > 0) {
            "$documentUrl?retry=$lastRetryAttempt"
        } else {
            documentUrl
        }

        ImageRequest.Builder(context)
            .data(uniqueDataUrl)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .size(Size.ORIGINAL)
            .crossfade(true)
            .build()
    }

    SubcomposeAsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight(),
        loading = if (showLoading) {
            {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else null,
        error = {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                IconButton(onClick = {
                    onRetry()
                }) {
                    Icon(Icons.Default.Refresh, "Retry")
                }
            }
        },
        success = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable { onImageSelected(documentUrl) }
            ) {
                this@SubcomposeAsyncImage.SubcomposeAsyncImageContent()
            }
        }
    )
}
