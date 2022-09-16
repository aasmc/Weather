package ru.aasmc.weather.util

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import timber.log.Timber

class GpsUtil(private val context: Context) {

    private val settingsClient: SettingsClient =
        LocationServices.getSettingsClient(context)
    private val locationSettingsRequest: LocationSettingsRequest?
    private val locationManager = context
        .getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationLiveData.locationRequest)

        locationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun turnGPSOn(onGpsListener: OnGpsListener?) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener?.gpsStatus(true)
        } else {
            locationSettingsRequest?.let { request ->
                settingsClient
                    .checkLocationSettings(request)
                    .addOnSuccessListener(context as Activity) {
                        onGpsListener?.gpsStatus(true)
                    }
                    .addOnFailureListener(context) { exception ->
                        when ((exception as ApiException).statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                try {
                                    val resApiException =
                                        exception as ResolvableApiException
                                    resApiException.startResolutionForResult(
                                        context,
                                        GPS_REQUEST_CHECK_SETTINGS
                                    )
                                } catch (sendIntentException: Exception) {
                                    sendIntentException.printStackTrace()
                                    Timber.i("PendingIntent unable to execute request.")
                                }
                            }

                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                val errorMessage =
                                    "Location settings are inadequate, and cannot be fixed here. Please, fix in Settings."
                                Timber.e(errorMessage)
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
            }
        }
    }

    interface OnGpsListener {
        fun gpsStatus(isGPSEnabled: Boolean)
    }
}