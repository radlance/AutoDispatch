package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.writeToFile
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
import platform.UIKit.UIDocumentInteractionControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberFileSaver(): FileSaver {
    return remember {
        object : FileSaver {
            override fun saveAndOpen(fileName: String, bytes: ByteArray) {
                val data = bytes.toNSData()
                val tempDir = NSTemporaryDirectory()
                val path = tempDir + fileName
                data.writeToFile(path, true)

                val url = NSURL.fileURLWithPath(path)
                val controller = UIDocumentInteractionController.interactionControllerWithURL(url)

                val rootViewController =
                    UIApplication.sharedApplication.keyWindow?.rootViewController
                if (rootViewController != null) {
                    controller.delegate =
                        object : NSObject(), UIDocumentInteractionControllerDelegateProtocol {
                            override fun documentInteractionControllerViewControllerForPreview(
                                controller: UIDocumentInteractionController
                            ): UIViewController = rootViewController
                        }
                    controller.presentPreviewAnimated(true)
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = if (isEmpty()) NSData() else {
    this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}
