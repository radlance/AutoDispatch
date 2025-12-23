package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun rememberCameraLauncher(
    onResult: (ByteArray?) -> Unit
): CameraLauncher {
    return remember { IosCameraLauncher(onResult) }
}

private class IosCameraLauncher(
    private val onResult: (ByteArray?) -> Unit
) : CameraLauncher {

    private val imagePicker = UIImagePickerController()
    private val delegate = ImagePickerDelegate(onResult)

    init {
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            imagePicker.sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            imagePicker.allowsEditing = false
        }
        imagePicker.delegate = delegate
    }

    override fun capture() {
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
            controller?.presentViewController(imagePicker, animated = true, completion = null)
        } else {
            println("Camera not available on this device/simulator")
            onResult(null)
        }
    }

    private class ImagePickerDelegate(
        private val onResult: (ByteArray?) -> Unit
    ) : NSObject(), UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val image =
                didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            val jpegData =
                image?.let { UIImageJPEGRepresentation(it, 1.0) }

            val byteArray = jpegData?.toByteArray()

            picker.dismissViewControllerAnimated(true) {
                onResult(byteArray)
            }
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true) {
                onResult(null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}