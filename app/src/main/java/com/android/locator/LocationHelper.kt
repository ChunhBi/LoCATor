import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

const val NEW_LOCATION_SECOND_THRESHOLD = 1
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    interface LocationCallback {
        fun onLocationResult(location: Location)
        fun onLocationUnavailable()
    }

    fun getCurrentLocation(callback: LocationCallback) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback.onLocationUnavailable()
            return
        }

        // Request last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Got last known location
                    val locationTime = location.time // Timestamp in milliseconds since epoch

                    val currentTime = System.currentTimeMillis()
                    // Calculate the age of the location in milliseconds
                    val locationAge = currentTime - locationTime
                    // Convert to desired unit (e.g., seconds)
                    val locationAgeInSec = locationAge / 1000.0

                    // Check if location is too old based on your threshold
                    if (locationAgeInSec > NEW_LOCATION_SECOND_THRESHOLD) {
                        fusedLocationClient.requestLocationUpdates(createLocationRequest(), object :
                            com.google.android.gms.location.LocationCallback() {
                            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                                super.onLocationResult(locationResult)
                                // Use the most recent location from the location result
                                callback.onLocationResult(locationResult.lastLocation)
                                // Remove location updates as we only need one-time location
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }, null)
                    } else {
                        callback.onLocationResult(location)
                    }
                } else {
                    // Last known location not available, request location updates
                    fusedLocationClient.requestLocationUpdates(createLocationRequest(), object :
                        com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                            super.onLocationResult(locationResult)
                            // Use the most recent location from the location result
                            callback.onLocationResult(locationResult.lastLocation)
                            // Remove location updates as we only need one-time location
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }, null)
                }
            }
    }

    private fun createLocationRequest(): com.google.android.gms.location.LocationRequest {
        return com.google.android.gms.location.LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
        }
    }
}
