package net.xenloops.wifiWarDriver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import net.xenloops.wifiWarDriver.databinding.ActivityMainBinding
import java.text.DecimalFormat

object Location {
    private val locationPermissionCode = 1000
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private val df = DecimalFormat("#.###")
    var latitude = 0.0
    var longitude = 0.0

    fun getLastLocation(context: Context, activity: MainActivity, binding: ActivityMainBinding) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                this.latitude = location.latitude
                this.longitude = location.longitude
                binding.textCoordinates.text = "Lat: " + df.format(this.latitude) + " Lon: " + df.format(this.longitude)
            } else {
                binding.textCoordinates.text = "Coordinates unavailable"
            }
        }
    }

}