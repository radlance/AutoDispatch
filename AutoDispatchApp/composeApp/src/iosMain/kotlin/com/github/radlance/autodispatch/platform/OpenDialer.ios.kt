package com.github.radlance.autodispatch.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openDialer(phoneNumber: String, context: Any?) {
    val url = NSURL(string = "tel:$phoneNumber")
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}