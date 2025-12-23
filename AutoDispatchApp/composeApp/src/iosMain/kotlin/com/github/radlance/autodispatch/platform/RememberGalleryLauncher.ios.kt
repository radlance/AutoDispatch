package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberGalleryLauncher(
    onResult: (ByteArray?) -> Unit
): GalleryLauncher {
    val delegate = remember { GalleryDelegate(onResult) }

    return remember {
        object : GalleryLauncher {
            override fun pick() {
                val configuration = PHPickerConfiguration().apply {
                    setFilter(PHPickerFilter.imagesFilter)
                    setSelectionLimit(1)
                }

                val picker = PHPickerViewController(configuration)
                picker.delegate = delegate

                val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
                controller?.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

private class GalleryDelegate(
    private val onResult: (ByteArray?) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        dispatch_async(dispatch_get_main_queue()) {
            picker.dismissViewControllerAnimated(true, null)
        }

        val result = didFinishPicking.firstOrNull() as? PHPickerResult
            ?: return onResult(null)

        val provider = result.itemProvider

        if (!provider.hasItemConformingToTypeIdentifier(UTTypeItem.identifier)) {
            onResult(null)
            return
        }

        provider.loadDataRepresentationForTypeIdentifier(
            UTTypeItem.identifier
        ) { data, _ ->
            if (data == null) {
                dispatch_async(dispatch_get_main_queue()) { onResult(null) }
                return@loadDataRepresentationForTypeIdentifier
            }

            val image = UIImage(data = data)
            val processed = processImage(image)

            dispatch_async(dispatch_get_main_queue()) {
                onResult(processed)
            }
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun processImage(image: UIImage): ByteArray? {
        val originalWidth = image.size.useContents { width }
        val originalHeight = image.size.useContents { height }
        val side = minOf(originalWidth, originalHeight)

        val x = (originalWidth - side) / 2.0
        val y = (originalHeight - side) / 2.0

        val cropRect = CGRectMake(x, y, side, side)
        val cgImage = image.CGImage()?.let {
            platform.CoreImage.CIImage.imageWithCGImage(it).imageByCroppingToRect(cropRect)
            platform.CoreGraphics.CGImageCreateWithImageInRect(it, cropRect)
        } ?: return null

        val squareImage =
            UIImage.imageWithCGImage(cgImage, scale = 1.0, orientation = image.imageOrientation)

        val resized = squareImage.resize(512.0, 512.0)

        val data = UIImageJPEGRepresentation(resized, 0.85)
        return data?.toByteArray()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.resize(width: Double, height: Double): UIImage {
    val size = CGSizeMake(width, height)
    UIGraphicsBeginImageContextWithOptions(size, false, 1.0)
    drawInRect(CGRectMake(0.0, 0.0, width, height))
    val result = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    return result ?: this
}