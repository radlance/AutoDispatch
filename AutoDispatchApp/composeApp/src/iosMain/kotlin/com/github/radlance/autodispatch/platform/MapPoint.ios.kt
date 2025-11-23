package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
@Composable
actual fun MapPoint(address: String, onDismiss: () -> Unit) {
    val app = UIApplication.sharedApplication
    val controller = app.keyWindow?.rootViewController
        ?: return

    val nsString = NSString.create(string = address)
    val encoded = nsString.stringByAddingPercentEncodingWithAllowedCharacters(
        NSCharacterSet.URLQueryAllowedCharacterSet
    ) ?: address

    val sheet = UIAlertController.alertControllerWithTitle(
        "Открыть в картах",
        message = null,
        preferredStyle = UIAlertControllerStyleActionSheet
    )

    sheet.addAction(
        UIAlertAction.actionWithTitle(
            "Apple Maps",
            style = UIAlertActionStyleDefault
        ) {
            val url = NSURL(string = "http://maps.apple.com/?q=$encoded")
            app.openURL(
                url = url,
                options = emptyMap<Any?, Any>(),
                completionHandler = {}
            )
            onDismiss()
        }
    )

    val googleUrl = NSURL(string = "comgooglemaps://")
    if (app.canOpenURL(googleUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "Google Maps",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "comgooglemaps://?q=$encoded")
                app.openURL(
                    url = url,
                    options = emptyMap<Any?, Any>(),
                    completionHandler = {}
                )
                onDismiss()
            }
        )
    }

    val yandexUrl = NSURL(string = "yandexmaps://")
    if (app.canOpenURL(yandexUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "Яндекс Карты",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "yandexmaps://maps.yandex.ru/?text=$encoded")
                app.openURL(
                    url = url,
                    options = emptyMap<Any?, Any>(),
                    completionHandler = {}
                )
                onDismiss()
            }
        )
    }

    val twoGisUrl = NSURL(string = "dgis://")
    if (app.canOpenURL(twoGisUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "2ГИС",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "dgis://2gis.ru/search/$encoded")
                app.openURL(
                    url = url,
                    options = emptyMap<Any?, Any>(),
                    completionHandler = {}
                )
                onDismiss()
            }
        )
    }

    sheet.addAction(
        UIAlertAction.actionWithTitle(
            "Отмена",
            style = UIAlertActionStyleCancel
        ) { onDismiss() }
    )

    controller.presentViewController(sheet, animated = true, completion = null)
}