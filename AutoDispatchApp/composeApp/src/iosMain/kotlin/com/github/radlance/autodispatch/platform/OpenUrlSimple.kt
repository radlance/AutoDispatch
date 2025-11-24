package com.github.radlance.autodispatch.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun UIApplication.openUrlSimple(url: NSURL) {
    openURL(url, emptyMap<Any?, Any>()) {}
}