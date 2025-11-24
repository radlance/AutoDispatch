package com.github.radlance.autodispatch.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openDialer(phoneNumber: String, context: Any?) {
    val url = NSURL(string = "tel://$phoneNumber")
    val app = UIApplication.sharedApplication

    if (app.canOpenURL(url)) {
        app.openUrlSimple(url = url)
    }
}