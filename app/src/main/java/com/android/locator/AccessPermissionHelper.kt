import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AccessPermissionHelper() {



    companion object {
        private lateinit var activity: Activity
        const val LOCATION_PERMISSION_REQUEST_CODE = 101
        const val CAMERA_PERMISSION_REQUEST_CODE = 102
        const val GALLERY_PERMISSION_REQUEST_CODE = 103

        fun requestLocationPermission() {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        fun requestCameraPermission() {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        fun requestGalleryPermission() {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
            }
        }
        fun setActivity(a:Activity){
            activity=a
        }

        fun isLocationPermissionGranted(): Boolean {
            return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        fun isCameraPermissionGranted(): Boolean {
            return isPermissionGranted(Manifest.permission.CAMERA)
        }

        fun isGalleryPermissionGranted(): Boolean {
            return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        private fun isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }



}
