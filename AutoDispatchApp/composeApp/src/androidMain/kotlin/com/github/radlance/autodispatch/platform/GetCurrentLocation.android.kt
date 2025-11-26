package com.github.radlance.autodispatch.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.github.radlance.autodispatch.delivery.route.domain.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine

actual suspend fun getCurrentLocation(context: Any?): Location? {

    val con = context as? Context ?: return null

    val fine = ContextCompat.checkSelfPermission(
        con, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!fine) return null

    return suspendCancellableCoroutine { cont ->
        val provider = LocationServices.getFusedLocationProviderClient(con)

        provider.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                cont.resume(value = Location(loc.latitude, loc.longitude)) { _, _, _ ->

                }
            } else {
                provider.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener { fresh ->
                    cont.resume(
                        fresh?.let { Location(it.latitude, it.longitude) }
                    ) { _, _, _ -> }
                }
            }
        }
    }
}
