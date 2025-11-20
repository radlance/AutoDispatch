package com.github.radlance.autodispatch.platform

import kotlinx.cinterop.BetaInteropApi
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

@OptIn(BetaInteropApi::class)
actual fun openMap(address: String, context: Any?) {
    val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
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
            UIApplication.sharedApplication.openURL(url)
        }
    )

    val googleUrl = NSURL(string = "comgooglemaps://")
    if (UIApplication.sharedApplication.canOpenURL(googleUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "Google Maps",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "comgooglemaps://?q=$encoded")
                UIApplication.sharedApplication.openURL(url)
            }
        )
    }

    val yandexUrl = NSURL(string = "yandexmaps://")
    if (UIApplication.sharedApplication.canOpenURL(yandexUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "Яндекс.Карты",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "yandexmaps://maps.yandex.ru/?text=$encoded")
                UIApplication.sharedApplication.openURL(url)
            }
        )
    }

    val twoGisUrl = NSURL(string = "dgis://")
    if (UIApplication.sharedApplication.canOpenURL(twoGisUrl)) {
        sheet.addAction(
            UIAlertAction.actionWithTitle(
                "2ГИС",
                style = UIAlertActionStyleDefault
            ) {
                val url = NSURL(string = "dgis://2gis.ru/search/$encoded")
                UIApplication.sharedApplication.openURL(url)
            }
        )
    }

    sheet.addAction(
        UIAlertAction.actionWithTitle(
            "Отмена",
            style = UIAlertActionStyleCancel
        ) {}
    )

    controller.presentViewController(sheet, animated = true, completion = null)
}