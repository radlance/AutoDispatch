package com.github.radlance.autodispatch.platform

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapRouteDialog(lat: Double, lon: Double, onDismiss: () -> Unit) {
    val app = UIApplication.sharedApplication
    val controller = app.keyWindow?.rootViewController
        ?: return

    val sheet = UIAlertController.alertControllerWithTitle(
        "Построить маршрут через",
        message = null,
        preferredStyle = UIAlertControllerStyleActionSheet
    )

    sheet.addAction(
        UIAlertAction.actionWithTitle(
            "Apple Maps",
            style = UIAlertActionStyleDefault
        ) {
            val url = NSURL(string = "http://maps.apple.com/?daddr=$lat,$lon&dirflg=d")
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
                val url = NSURL(string = "comgooglemaps://?daddr=$lat,$lon&directionsmode=driving")
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
                val url =
                    NSURL(string = "yandexmaps://build_route_on_map?lat_to=$lat&lon_to=$lon&what=auto")
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
                val url =
                    NSURL(string = "yandexmaps://build_route_on_map?lat_to=$lat&lon_to=$lon&what=auto")
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