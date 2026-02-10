package com.github.radlance.autodispatch.request.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import coil3.size.Size
import com.github.radlance.autodispatch.common.presentation.SimpleCustomDialog
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenImageDialog(
    onDismissRequest: () -> Unit,
    selectedImageUrl: String?,
    onChangeImageIconClick: (document: String) -> Unit,
    modifier: Modifier = Modifier,
    documents: List<String>? = null
) {
    val context = LocalPlatformContext.current
    SimpleCustomDialog(
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismissRequest
    ) { requestDismiss ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(selectedImageUrl)
                        .size(Size.ORIGINAL)
                        .scale(Scale.FILL)
                        .crossfade(true)
                        .build(),
                )
                val zoomState = rememberZoomState(contentSize = painter.intrinsicSize)

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                        .zoomable(zoomState),
                )

                IconButton(
                    onClick = requestDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null
                    )
                }
                documents?.let {
                    val indexOf = documents.indexOf(selectedImageUrl)
                    if (indexOf > 0) {
                        IconButton(
                            onClick = { onChangeImageIconClick(documents[indexOf - 1]) },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp)
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.rotate(180f)
                            )
                        }
                    }
                    if (indexOf != documents.size - 1) {
                        IconButton(
                            onClick = { onChangeImageIconClick(documents[indexOf + 1]) },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(16.dp)
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowForwardIos,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}